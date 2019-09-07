数据库版本
1：备忘信息表(开始日期,结束日期,开始时间,结束时间,标题,内容,分类)
memo_creator="create table memo_table(create_datetime datetime primary key,start_date,end_data," +
              "start_time,end_time,memo_title,memo_content text,memo_classification default '未分类')";

图标点击样式：点击后尺寸变为原来的0.9倍

adb connect 127.0.0.1:7555 //mumu模拟器连接

F:\android-sdk_r24.4.1-windows\platform-tools //adb路径