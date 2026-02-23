package com.fantasy.fm.common.enums;

import com.fantasy.fm.common.constant.MusicConstant;
import com.fantasy.fm.common.exception.NotSupportAudioFormatException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AudioFormatEnum {

    MP3("mp3"),
    WAV("wav"),
    FLAC("flac"),
    AAC("aac"),
    OGG("ogg"),
    M4A("m4a");

    @Getter
    private final String ext;

    public static void checkExt(String ext) {
        //获取枚举值
        AudioFormatEnum[] values = AudioFormatEnum.values();
        for (AudioFormatEnum value : values) {
            //如果文件扩展名匹配则返回对应的枚举
            if (value.getExt().equalsIgnoreCase(ext)) {
                return;
            }
        }
        throw new NotSupportAudioFormatException(MusicConstant.NOT_SUPPORT_AUDIO_FORMAT + ext);
    }
}

