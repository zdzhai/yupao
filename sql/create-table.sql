-- auto-generated definition
create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)                       null comment '用户昵称',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '用户密码',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 null comment '状态0-正常',
    phone        varchar(128)                       null comment '电话',
    creatTime    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     int      default 0                 null comment '逻辑删除 0 不删除 1删除',
    userRole     int      default 0                 null comment '用户角色：0 普通用户 1管理员',
    planetCode   varchar(512)                       null comment '星球编号'
)
    comment '用户表';

