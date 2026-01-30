package com.fantasy.fm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.constant.MusicConstant;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFileInfo(File musicFile, String fileHash) {
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
                    .build();
            //保存音乐基本信息到数据库
            musicMapper.insert(musicInfo);
            musicId = musicInfo.getId();
        } catch (Exception e) {
            log.error("读取音乐文件元数据失败: ", e);
        }
        //保存音乐文件信息到数据库
        MusicFileInfo mfi = MusicFileInfo.builder()
                .musicId(musicId)
                .fileName(musicFile.getName())
                .filePath(musicFile.getAbsolutePath())
                .fileSize(musicFile.length())
                .fileType(musicFile.getName().substring(musicFile.getName().lastIndexOf(".") + 1))
                .uploadTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .fileHash(fileHash)
                .build();
        musicManagerMapper.insert(mfi);
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
    public void deleteByMusicId(Long musicId) {
        log.info("Deleting music. ID: {}", musicId);
        // 删除音乐基本信息
        musicMapper.deleteById(musicId);
        // 使用LambdaQueryWrapper删除音乐文件信息,根据musicId字段
        musicManagerMapper.delete(
                new LambdaQueryWrapper<MusicFileInfo>()
                        .eq(MusicFileInfo::getMusicId, musicId));
        //同时删除对应的MusicListTrack记录
        musicListTrackMapper.delete(new LambdaQueryWrapper<MusicListTrack>()
                .eq(MusicListTrack::getMusicId, musicId));
    }

    @Override
    public PageDTO<MusicVO> queryMusicPage(MusicPageQuery query) {
        // 构建分页查询对象
        Page<Music> page = Page.of(query.getPageNum(), query.getPageSize());
        // 执行分页查询
        page = musicMapper.selectPage(page, null);
        // 将分页结果转换为 PageDTO<MusicVO>并返回
        return PageDTO.of(page, MusicVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteMusicByIds(List<Long> ids) {
        //删除音乐信息
        musicMapper.deleteByIds(ids);
        //删除音乐文件信息
        musicManagerMapper.delete(new LambdaQueryWrapper<MusicFileInfo>()
                .in(MusicFileInfo::getMusicId, ids));
        //删除对应的MusicListTrack记录
        musicListTrackMapper.delete(new LambdaQueryWrapper<MusicListTrack>()
                .in(MusicListTrack::getMusicId, ids));
    }
}
