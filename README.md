# 项目介绍

这是一个在需要本地化部署地图服务时，快速建立本地瓦片地图服务的小工具。

本工具基于 Springboot 进行编写，以接口的方式提供瓦片地图服务。

## 1 项目部署
### 1.1 配置文件修改

```yml
server:
  # 服务端口 (按需设置)
  port: 8200
  servlet:
    # 应用的访问路径
    context-path: /trs-map
  # 服务模块
  devtools:
    restart:
      # 热部署开关
      enabled: true

map-server:
  # 瓦片地图下载目录
  init-dirs:  map, map/google, map/google/satellite, map/google/image, map/google/terrain, map/amap, map/amap/satellite, map/amap/image, map/amap/cover, map/tianditu, map/tianditu/satellite, map/tianditu/image, map/tianditu/cover
  # 天地图请求访问 key，key 的类型为 浏览器端。多个 key 之间用 英文逗号 隔开。(按需设置，注意格式)
  tiandituKeys: 22d7d4b663a4499547ce0adb4ff19b4b,c8cdfa08d6fa5651b8bba2f47a7a0c5c,974c0fcf7f47b26ccea88fd296c56bfb,9fd159d68457d255695a43c78e45d450
  # 地图瓦片下载的最大等级，超过该等级的瓦片不会被下载至本地，系统处于联网状态仍能显示该瓦片。(按需设置)
  maxLevel: 10
  # 瓦片全部下载开关，设置为 true 时，请求的所有地图瓦片都将被下载至本地。 (按需设置，谨慎开启)
  saveAll: false

# 日志配置
logging:
  level:
    cn.com.trs.MapServer: debug
    org.springframework: warn

```



### 1.2 服务启动与停止

1. 提供 script/startup.sh 脚本供服务启动
2. 提供 script/shutdown.sh 脚本供停止服务
3. 采用常规 jar 包的 启动、停止 命令亦可

> 若使用脚本进行服务的启动和停止，脚本和 jar 包需在同一个目录下。

--------

## 2 支持地图类型

| 标识字段           | 地图类型        | 目录地址                |
| ------------------ | --------------- | ----------------------- |
| google-satellite   | Google-卫星图   | map/google/satellite/   |
| google-image       | Google-矢量底图 | map/google/image        |
| google-terrain     | Google-地形图   | map/google/terrain/     |
| amap-satellite     | 高德-卫星图     | map/amap/satellite/     |
| amap-image         | 高德-矢量底图   | map/amap/image/         |
| amap-cover         | 高德-标注底图   | map/amap/cover/         |
| tianditu-satellite | 天地图-卫星图   | map/tianditu/satellite/ |
| tianditu-cover     | 天地图-标注底图 | map/tianditu/cover/     |
| tianditu-image     | 天地图-矢量底图 | map/tianditu/image/     |

--------

## 3 API简述

### 3.1 /server/{type}/{x}/{y}/{z}

- 请求地图瓦片，`GET` 类型
- 参数：
  1. `type`：地图瓦片类型标识字段。String 类型
  2. `x`：标准TMS(瓦片地图服务)的 x 参数，int 类型
  3. `y`: 标准TMS(瓦片地图服务)的 y 参数，int 类型
  4. `z`: 标准TMS(瓦片地图服务)的 z 参数，int 类型
- 返回值：地图瓦片的 byte 数组
- 请求示例：/server/tianditu-image/0/0/1

> 注意 `saveAll` 和 `maxLevel` 两个参数的限制
>
> 1. 当 `!saveAll && z > maxLevel`
>    1. 请求的瓦片本地已存，返回该瓦片
>    2. 请求的瓦片本地未存，向远程服务器请求该瓦片并返回，且本地不会保存该瓦片
> 2. 其他情况：
>    1. 请求的瓦片本地已存，返回该瓦片
>    2. 请求的瓦片本地未存，向远程服务器请求该瓦片并保存至本地，返回该瓦片



### 3.2 /server/initMap

- 地图瓦片数据初始化，将各级所有瓦片下载至本地。`POST` 类型。
- 参数：form-data 格式
  1. `startLevel`：标准TMS(瓦片地图服务)的 z 参数，int 类型。
  2. `types`：地图瓦片类型数组，元素为 地图类型标识字段
- 返回值：执行结果
  - startLevel：地图起始级别
  - maxLevel：地图最大级别
  - errorTileSum：下载出错的瓦片总数
  - errorTilePositions：下载出错的瓦片坐标集合

> 该接口以 startLevel 为初始地图瓦片级别，至 最大级别 结束，依次下载 各级别 所有的地图瓦片，并将下载出错的瓦片坐标进行返回。



### 3.3 /server/initTile

- 地图瓦片批量下载，将指定瓦片下载至本地。`POST` 类型。

- 参数：JsonBody

  1. `tilePositions`：瓦片信息数组
  2. `types`：地图瓦片类型数组

  ```json
  {
      "tilePositions": [
          {
              "x": 5,
              "y": 5,
              "z": 5
          },
          {
              "x": 6,
              "y": 6,
              "z": 6
          }
      ],
      "types": ["tianditu-image", "tianditu-cover"]
  }
  ```

- 返回值：执行结果

  - errorTilePositions：下载出错的瓦片信息列表
  - errorTileSum：下载出错的瓦片总数

> 该接口不受 maxLevel 和 saveAll 的限制



# 注意事项

## 1 瓦片数计算

若当前地图瓦片的级别为 `level`，则该层瓦片的总数为
$$
2^{level} * 2^{level}
$$
每张瓦片的大小基本为 1KB，因此，设置 `maxLevel` 时，注意考虑地图瓦片需要的存储空间。

---

## 2 天地图相关限制

通过天地图下载瓦片必须使用天地图的 key，且 key 的类型为 `浏览器端`。

每个 key 每天的访问限额为 1 万次。

---

## 3 文件的保存

由于本系统需要进行文件读取操作，请注意设置地图瓦片所在目录的访问权限，避免地图瓦片下载后无法进行保存。

