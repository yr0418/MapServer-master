package com.yr.MapServer.controller;

import com.yr.MapServer.domain.RequestTiles;
import com.yr.MapServer.domain.TilePosition;
import com.yr.MapServer.utils.InitUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/server")
public class ServerController {

    /**
     * 地图瓦片最大级别
     */
    @Value("${map-server.maxLevel}")
    private Integer maxLevel;

    /**
     * 地图瓦片保存全部开关
     */
    @Value(("${map-server.saveAll}"))
    private Boolean saveAll;
    @Autowired
    private InitUtils initUtils;

    /**
     * 请求地图瓦片
     *
     * @param x    地图瓦片 x 坐标
     * @param y    地图瓦片 y 坐标
     * @param z    地图瓦片级别
     * @param type 地图瓦片类型
     *
     * @return 二进制地图瓦片数据
     */
    @RequestMapping(
            value = "/{type}/{x}/{y}/{z}",
            method = RequestMethod.GET,
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE}
    )
    public byte[] initMap (@PathVariable Integer x, @PathVariable Integer y,
                           @PathVariable Integer z, @PathVariable String type) throws IOException {
        // 未开启保存全部且超过最大等级设置的瓦片，直接返回地图瓦片二级制数据，不保存至本地
        if (!saveAll && z > maxLevel) {
            return initUtils.getImage(InitUtils.getTypeByName(type), x, y, z);
        }
        else {
            String path = InitUtils.getPathByType(InitUtils.getTypeByName(type));
            String imgPath = path + z + "/" + x + "/" + y + "/img.png";
            File file = new File(imgPath);
            // 本地未含有该瓦片，连接远程服务器下载该瓦片
            if (! file.exists()) {
                initUtils.saveImage(InitUtils.getTypeByName(type), x, y, z);
            }
            FileInputStream inputStream = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            inputStream.read(data);
            inputStream.close();
            return data;
        }
    }

    /**
     * 地图瓦片初始化
     *
     * @param startLevel 开始级别
     * @param types      地图瓦片类型列表
     * @return 执行结果
     */
    @PostMapping(value = "/initMap")
    public HashMap<String, Object> initMap (Integer startLevel, String[] types) {
        HashMap<String, Object> result = new HashMap<>();
        List<TilePosition> errorTilePositions = new ArrayList<>();
        result.put("startLevel", startLevel);
        result.put("maxLevel", maxLevel);
        if (startLevel <= maxLevel) {
            for (int z = startLevel; z <= maxLevel; z++) {
                int end = (int) (Math.pow(2, z));
                for (int x = 0; x < end; x++) {
                    for (int y = 0; y < end; y++) {
                        for (String type : types) {
                            if (!initUtils.saveImage(InitUtils.getTypeByName(type), x, y, z)) {
                                // 记录下载未成功的瓦片的坐标信息
                                errorTilePositions.add(new TilePosition(x, y, z));
                            }
                        }
                    }
                }
            }
            result.put("message", "操作成功");
            result.put("errorTileSum", errorTilePositions.size());
            result.put("errorTilePositions", errorTilePositions);
        } else {
            result.put("message", "开始级别超过设置的最大级别");
        }
        return result;
    }

    /**
     * 地图瓦片批量下载
     *
     * @return 执行结果
     */
    @PostMapping(value = "/initTile")
    public HashMap<String, Object> initTile(@RequestBody RequestTiles requestTiles) {
        HashMap<String, Object> result = new HashMap<>();
        List<TilePosition> errorTilePositions = new ArrayList<>();
        for (TilePosition tilePosition : requestTiles.getTilePositions()) {
            for (String type : requestTiles.getTypes()) {
                if (!initUtils.saveImage(InitUtils.getTypeByName(type), tilePosition.getX(), tilePosition.getY(), tilePosition.getZ())) {
                    errorTilePositions.add(tilePosition);
                }
            }
        }
        result.put("errorTileSum", errorTilePositions.size());
        result.put("errorTilePositions", errorTilePositions);
        return result;
    }
}
