create table music
(
    id           bigint auto_increment
        primary key,
    title        varchar(255) not null comment '标题',
    artist       varchar(255) not null comment '歌手',
    album        varchar(255) null comment '专辑',
    duration_ms  bigint       not null comment '歌曲时长(毫秒)',
    release_year varchar(20)  null comment '发行日期',
    cover_url    varchar(500) null comment '封面URL'
)
    comment '音乐信息表';

create table music_file_info
(
    id          bigint auto_increment
        primary key,
    music_id    bigint       not null comment '音乐实体ID',
    file_name   varchar(255) not null comment '文件名',
    file_path   varchar(500) not null comment '文件路径',
    file_size   bigint       not null comment '文件大小(字节)',
    file_type   varchar(10)  null comment '文件类型',
    upload_time datetime     not null comment '上传时间',
    create_time datetime     not null comment '创建时间',
    update_time datetime     null comment '更新时间',
    file_hash   varchar(64)  null comment '文件内容哈希值'
)
    comment '音乐文件信息表';

create index idx_file_hash
    on music_file_info (file_hash);

create table music_list
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    title       varchar(100)                       not null comment '歌单标题',
    description varchar(255)                       null comment '歌单简介',
    cover       varchar(255)                       null comment '封面图片URL',
    user_id     bigint                             not null comment '创建者用户ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '歌单表';

create table music_list_track
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    music_list_id bigint                             not null comment '歌单ID',
    music_id      bigint                             not null comment '音乐ID',
    join_time     datetime default CURRENT_TIMESTAMP null comment '加入歌单的时间',
    constraint uniq_music_list_track
        unique (music_list_id, music_id)
)
    comment '歌单曲目关联表';

create table user
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    username         varchar(50)                        not null comment '用户名',
    nickname         varchar(50)                        null comment '昵称',
    email            varchar(100)                       null comment '邮箱',
    password         varchar(100)                       not null comment '密码（加密存储）',
    avatar_url       varchar(255)                       null comment '头像URL',
    user_level_value int      default 0                 null comment '用户等级',
    created_time     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_username
        unique (username),
    constraint user_pk
        unique (email)
)
    comment '用户表';


