package com.fantasy.fm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.mapper.MusicManagerMapper;
import com.fantasy.fm.pojo.entity.MusicFileInfo;
import com.fantasy.fm.service.MusicManagerServer;
import org.springframework.stereotype.Service;

/**
 * MusicManagerServer 服务实现类
 */
@Service
public class MusicManagerServerImpl extends ServiceImpl<MusicManagerMapper, MusicFileInfo> implements MusicManagerServer {

}
