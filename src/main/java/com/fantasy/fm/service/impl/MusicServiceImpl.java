package com.fantasy.fm.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.constant.MusicConstant;
import com.fantasy.fm.constant.RedisCacheConstant;
import com.fantasy.fm.domain.dto.PageDTO;
import com.fantasy.fm.domain.entity.MusicListTrack;
import com.fantasy.fm.domain.query.MusicPageQuery;
import com.fantasy.fm.domain.vo.MusicVO;
import com.fantasy.fm.mapper.MusicListTrackMapper;
import com.fantasy.fm.mapper.MusicMapper;
import com.fantasy.fm.mapper.MusicManagerMapper;
import com.fantasy.fm.domain.entity.Music;
import com.fantasy.fm.domain.entity.MusicFileInfo;
import com.fantasy.fm.service.MusicService;
import com.fantasy.fm.utils.OSSUtil;
import com.fantasy.fm.utils.RedisCacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {

    private final MusicMapper musicMapper;
    private final MusicManagerMapper musicManagerMapper;
    private final MusicListTrackMapper musicListTrackMapper;
    private final RedisCacheUtil redisCacheUtil;
    private final OSSUtil ossUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisCacheConstant.MUSIC_INFO_CACHE, key = RedisCacheConstant.KEY_MUSIC_LIST),
            @CacheEvict(cacheNames = RedisCacheConstant.MUSIC_INFO_PAGE, allEntries = true),
            @CacheEvict(value = RedisCacheConstant.USER_MUSIC_LIST, allEntries = true),
            @CacheEvict(value = RedisCacheConstant.MUSIC_LIST_DETAIL, allEntries = true),
            @CacheEvict(value = RedisCacheConstant.MUSIC_COVER_CACHE, allEntries = true)
    })
    public void saveFileInfo(File musicFile, String fileHash, String ossUrl) {
        //获取音乐元数据信息
        AudioFile audioFile;
        Music musicInfo;
        Long musicId = null;
        try {
            audioFile = AudioFileIO.read(musicFile);
            Tag tag = audioFile.getTag();
            tag.getFirst(FieldKey.YEAR);
            musicInfo = Music.builder()
                    .title(StrUtil.isNotBlank(tag.getFirst(FieldKey.TITLE)) ? tag.getFirst(FieldKey.TITLE) : musicFile.getName())
                    .artist(StrUtil.isNotBlank(tag.getFirst(FieldKey.ARTIST)) ? tag.getFirst(FieldKey.ARTIST) : MusicConstant.UNKNOWN_ARTIST)
                    .album(StrUtil.isNotBlank(tag.getFirst(FieldKey.ALBUM)) ? tag.getFirst(FieldKey.ALBUM) : MusicConstant.UNKNOWN_ALBUM)
                    .durationMs(audioFile.getAudioHeader().getTrackLength() * 1000L)
                    .releaseYear(StrUtil.isNotBlank(tag.getFirst(FieldKey.YEAR)) ? tag.getFirst(FieldKey.YEAR) : MusicConstant.UNKNOWN_RELEASE_YEAR)
                    .coverUrl(getCoverUrl(tag, musicFile))
                    .build();
            //保存音乐基本信息到数据库
            musicMapper.insert(musicInfo);
            musicId = musicInfo.getId();
        } catch (Exception e) {
            log.error("读取音乐文件元数据失败: ", e);
        }
        //保存音乐文件信息到数据库
        String originalName = musicFile.getName().split("_")[2];
        log.info("文件名: {}", originalName);
        MusicFileInfo mfi = MusicFileInfo.builder()
                .musicId(musicId)
                .fileName(originalName)
                .filePath(ossUrl)
                .fileSize(musicFile.length())
                .fileType(musicFile.getName().substring(musicFile.getName().lastIndexOf(".") + 1))
                .uploadTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .fileHash(fileHash)
                .build();
        musicManagerMapper.insert(mfi);
    }

    /**
     * 获取封面图片并保存到OSS，返回封面图片路径
     */
    private String getCoverUrl(Tag tag, File musicFile) {
        //获取音乐目录
        Artwork artwork = tag.getFirstArtwork();
        if (artwork == null) {
            return null;
        }
        //创建cover目录
        /*File coverDir = new File(musicFile.getParent(), "cover");
        if (!coverDir.exists()) {
            coverDir.mkdirs();
        }*/
        //构建目标文件路径
        String originalName = musicFile.getName().split("_")[2];
        String coverDir = "cover/" + originalName + "_Cover.jpg";
        //读取封面图片数据
        byte[] data = artwork.getBinaryData();
        //保存封面图片到OSS
        return ossUtil.upload(data, coverDir);
    }

    @Override
    public ResponseEntity<Resource> playMusic(Long musicId) {
        // 获取音乐文件信息
        MusicFileInfo fileInfo = musicManagerMapper.getFileInfoByMusicId(musicId);
        File musicFile = new File(fileInfo.getFilePath());
        if (fileInfo.getFilePath().isBlank() || !musicFile.exists()) {
            log.error("音乐文件不存在，路径: {}", fileInfo.getFilePath());
            return ResponseEntity.notFound().build();
        }
        //创建资源对象
        Resource resource = new FileSystemResource(musicFile);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(resource);
    }

    @Override
    public ResponseEntity<Resource> downloadMusic(Long musicId) {
        // 获取音乐文件信息
        MusicFileInfo fileInfo = musicManagerMapper.getFileInfoByMusicId(musicId);
        File musicFile = new File(fileInfo.getFilePath());
        if (fileInfo.getFilePath().isBlank() || !musicFile.exists()) {
            log.error("音乐文件不存在，路径: {}", fileInfo.getFilePath());
            return ResponseEntity.notFound().build();
        }
        //处理下载文件名带"."的后缀
        String fileName = fileInfo.getFileName();
        int dotIndex = fileName.lastIndexOf('.');
        String nameWithoutExt = dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
        String ext = dotIndex == -1 ? "" : fileName.substring(dotIndex);
        String musicName = nameWithoutExt + ext;
        //创建资源对象
        Resource resource = new FileSystemResource(musicFile);
        //处理文件名中文乱码问题
        String encodeName = URLEncoder
                .encode(musicName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // 空格处理
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // 设置为二进制流下载
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodeName) // 设置下载文件名
                .body(resource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisCacheConstant.MUSIC_INFO_CACHE, key = RedisCacheConstant.KEY_MUSIC_LIST),
            @CacheEvict(cacheNames = RedisCacheConstant.MUSIC_INFO_PAGE, allEntries = true),
            @CacheEvict(value = RedisCacheConstant.USER_MUSIC_LIST, allEntries = true),
            @CacheEvict(value = RedisCacheConstant.MUSIC_LIST_DETAIL, allEntries = true),
            @CacheEvict(value = RedisCacheConstant.MUSIC_COVER_CACHE, allEntries = true)
    })
    public void deleteByMusicId(Long musicId) {
        log.info("Deleting music. ID: {}", musicId);
        // 删除封面图片文件
        deleteFile(musicMapper.selectById(musicId).getCoverUrl());
        // 删除音乐基本信息
        musicMapper.deleteById(musicId);
        // 使用LambdaQueryWrapper获取音乐文件信息,根据musicId字段
        LambdaQueryWrapper<MusicFileInfo> wrapper = new LambdaQueryWrapper<MusicFileInfo>()
                .eq(MusicFileInfo::getMusicId, musicId);
        MusicFileInfo musicFileInfo = musicManagerMapper.selectOne(wrapper);
        // 删除音乐文件及其对应记录
        deleteFile(musicFileInfo.getFilePath());
        musicManagerMapper.delete(wrapper);
        //同时删除对应的MusicListTrack记录
        musicListTrackMapper.delete(new LambdaQueryWrapper<MusicListTrack>()
                .eq(MusicListTrack::getMusicId, musicId));
    }

    private void deleteFile(String fileUrl) {
        //防止NPE
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }
        String objectName = null;
        try {
            URL url = new URL(fileUrl);
            objectName = url.getPath().substring(1); // 对象名:music/Kastra  - Fool For You.mp3
        } catch (MalformedURLException e) {
            log.error("urlPath提取失败!", e);
        }
        ossUtil.delete(objectName);
    }

    @Override
    @Cacheable(cacheNames = RedisCacheConstant.MUSIC_INFO_PAGE, key = "'pageNum:' + #query.pageNum + ':pageSize:' + #query.pageSize")
    public PageDTO<MusicVO> queryMusicPage(MusicPageQuery query) {
        // 构建分页查询对象
        Page<Music> page = Page.of(query.getPageNum(), query.getPageSize());
        // 执行分页查询
        page = musicMapper.selectPage(page, null);
        // 将分页结果转换为 PageDTO<MusicVO>并返回
        return PageDTO.of(page, MusicVO.class);
    }

    @Override
    public PageDTO<MusicVO> getMusicPageByCondition(MusicPageQuery query) {
        // 构建分页查询对象
        Page<Music> page = Page.of(query.getPageNum(), query.getPageSize());
        //构建查询条件
        LambdaQueryWrapper<Music> like = new LambdaQueryWrapper<Music>()
                .like(Music::getTitle, query.getTitle())
                .like(Music::getArtist, query.getArtist());
        // 执行分页查询
        page = musicMapper.selectPage(page, like);
        // 将分页结果转换为 PageDTO<MusicVO>并返回
        return PageDTO.of(page, MusicVO.class);
    }

    @Override
    public ResponseEntity<Resource> getMusicCoverById(Long id) {
        //只查询封面URL
        String coverUrl = getCoverUrl(id);

        //如果封面不存在，返回404
        if (coverUrl == null) {
            return ResponseEntity.notFound().build();
        }
        //创建资源对象
        FileSystemResource resource = new FileSystemResource(coverUrl);

        //检查资源文件是否存在
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 确定媒体类型
        String extension = coverUrl.substring(coverUrl.lastIndexOf(".") + 1).toLowerCase();
        MediaType mediaType = extension.equals("jpg") ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG;

        //添加 Last-Modified 头支持协商缓存 (304 Not Modified)
        long lasted = 0L;
        try {
            lasted = resource.lastModified();
        } catch (IOException e) {
            log.error("获取封面图片最后修改时间失败: {}", e.getMessage());
        }
        //返回封面图片资源
        return ResponseEntity.ok()
                //添加 HTTP 缓存头 (Cache-Control)
                // max-age=86400 秒 (1天), public 表示可以被中间代理缓存
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400, public") // 缓存一天
                .lastModified(lasted)
                .contentType(mediaType)
                .body(resource);
    }

    /**
     * 根据音乐ID获取封面URL
     */
    private String getCoverUrl(Long id) {
        String redisKey = RedisCacheConstant.MUSIC_COVER_CACHE + "::" + id;
        //尝试从缓存中获取封面URL
        String cover = redisCacheUtil.get(redisKey, String.class);
        if (cover != null) {
            log.info("从缓存中获取封面URL: {}", cover);
            return cover;
        }
        //缓存中没有则从数据库获取封面URL
        String coverUrl = musicMapper.selectOne(new LambdaQueryWrapper<Music>()
                .select(Music::getCoverUrl)
                .eq(Music::getId, id)).getCoverUrl();

        //将封面URL存入缓存
        if (coverUrl != null) {
            redisCacheUtil.set(redisKey, coverUrl);
        }

        return coverUrl;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisCacheConstant.MUSIC_INFO_CACHE, key = RedisCacheConstant.KEY_MUSIC_LIST),
            @CacheEvict(cacheNames = RedisCacheConstant.MUSIC_INFO_PAGE, allEntries = true),
            @CacheEvict(value = RedisCacheConstant.USER_MUSIC_LIST, allEntries = true),
            @CacheEvict(value = RedisCacheConstant.MUSIC_LIST_DETAIL, allEntries = true),
            @CacheEvict(value = RedisCacheConstant.MUSIC_COVER_CACHE, allEntries = true)
    })
    public void batchDeleteMusicByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        LambdaQueryWrapper<Music> musicWrapper = new LambdaQueryWrapper<Music>().in(Music::getId, ids);
        //删除封面图片文件
        List<Music> musicList = musicMapper.selectList(musicWrapper);
        for (Music music : musicList) {
            deleteFile(music.getCoverUrl());
        }
        //删除音乐信息
        musicMapper.delete(musicWrapper);
        //删除音乐文件信息
        LambdaQueryWrapper<MusicFileInfo> wrapper = new LambdaQueryWrapper<MusicFileInfo>()
                .in(MusicFileInfo::getMusicId, ids);
        List<MusicFileInfo> fileInfos = musicManagerMapper.selectList(wrapper);
        //删除对应的音乐文件
        for (MusicFileInfo fileInfo : fileInfos) {
            deleteFile(fileInfo.getFilePath());
        }
        musicManagerMapper.delete(wrapper);
        //删除对应的MusicListTrack记录
        musicListTrackMapper.delete(new LambdaQueryWrapper<MusicListTrack>()
                .in(MusicListTrack::getMusicId, ids));
    }

    @Override
    public List<MusicVO> getMusicInfo() {
        //构建redis缓存key
        String redisKey = RedisCacheConstant.MUSIC_INFO_CACHE + "::" + RedisCacheConstant.KEY_MUSIC_LIST;
        //尝试从缓存中获取音乐列表
        List<MusicVO> musicVOS = redisCacheUtil.get(redisKey, new TypeReference<List<MusicVO>>() {
        });
        // 缓存中有则直接返回
        if (musicVOS != null) {
            log.info("从缓存中获取音乐列表，数量：{}", musicVOS.size());
            return musicVOS;
        }

        //缓存中没有则从数据库获取音乐列表
        List<Music> list = this.list();
        if (list == null || list.isEmpty()) {
            return List.of();
        }

        List<MusicVO> listVO = list.stream().map(music -> {
            MusicVO vo = new MusicVO();
            BeanUtils.copyProperties(music, vo);
            return vo;
        }).toList();

        //将音乐列表存入缓存
        redisCacheUtil.set(redisKey, listVO);

        return listVO;
    }
}
