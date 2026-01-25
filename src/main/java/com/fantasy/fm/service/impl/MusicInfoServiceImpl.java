package com.fantasy.fm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.constant.MusicConstant;
import com.fantasy.fm.mapper.MusicInfoMapper;
import com.fantasy.fm.mapper.MusicManagerMapper;
import com.fantasy.fm.domain.entity.Music;
import com.fantasy.fm.domain.entity.MusicFileInfo;
import com.fantasy.fm.service.MusicInfoService;
import lombok.RequiredArgsConstructor;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MusicInfoServiceImpl extends ServiceImpl<MusicInfoMapper, Music> implements MusicInfoService {

    private final MusicInfoMapper musicInfoMapper;
    private final MusicManagerMapper musicManagerMapper;

    @Override
    public void saveFileInfo(File musicFile) {
        //获取音乐元数据信息
        AudioFile audioFile = null;
        Music musicInfo = null;
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
            musicInfoMapper.insert(musicInfo);
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
                .build();
        musicManagerMapper.insert(mfi);
    }
}
