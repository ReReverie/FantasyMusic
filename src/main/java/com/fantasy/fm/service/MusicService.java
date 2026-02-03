package com.fantasy.fm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.fm.domain.dto.PageDTO;
import com.fantasy.fm.domain.entity.Music;
import com.fantasy.fm.domain.query.MusicPageQuery;
import com.fantasy.fm.domain.vo.MusicVO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.List;

public interface MusicService extends IService<Music> {
    /**
     * 保存音乐文件信息
     *
     * @param musicFile 音乐文件
     * @param fileHash 文件哈希值
     */
    void saveFileInfo(File musicFile, String fileHash);

    /**
     * 播放音乐
     *
     * @param musicId 音乐ID
     */
    ResponseEntity<Resource> playMusic(Long musicId);

    /**
     * 下载音乐
     *
     * @param musicId 音乐ID
     */
    ResponseEntity<Resource> downloadMusic(Long musicId);

    /**
     * 根据音乐ID删除音乐
     *
     * @param musicId 音乐ID
     */
    void deleteByMusicId(Long musicId);

    /**
     * 分页查询音乐
     *
     * @param query 分页查询参数
     * @return 音乐列表
     */
    PageDTO<MusicVO> queryMusicPage(MusicPageQuery query);

    /**
     * 批量删除音乐
     *
     * @param ids 音乐ID列表
     */
    void batchDeleteMusicByIds(List<Long> ids);

    /**
     * 获取音乐信息列表
     */
    List<MusicVO> getMusicInfo();

    /**
     * 根据条件分页查询音乐
     *
     * @param query 查询条件
     * @return 音乐分页结果
     */
    PageDTO<MusicVO> getMusicPageByCondition(MusicPageQuery query);

    /**
     * 根据音乐ID获取音乐封面
     *
     * @param id 音乐ID
     * @return 音乐封面资源
     */
    ResponseEntity<Resource> getMusicCoverById(Long id);
}
