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
    planetCode   varchar(512)                       null comment '星球编号',
    tags         varchar(1024)                      null comment '标签 json列表',
    profile      varchar(512)                       null comment '个人简介'
)
    comment '用户表';


alter table user add column tags varchar(1024) null comment '标签列表';


-- 用户队伍关系
create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint comment '用户id',
    teamId     bigint comment '队伍id',
    joinTime   datetime null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0 not null comment '是否删除'
) comment '用户队伍关系';


--        1.查询队伍和队伍创建人的信息
        -- select * from team t left join user u on t.userId = u.id;
--         关联查询已加入队伍的用户信息
--         select * from team t join user_team ut on t.id = ut.teamId
--         left join user u on ut.userId = u.id;