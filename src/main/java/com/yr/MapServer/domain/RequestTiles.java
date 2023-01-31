package com.yr.MapServer.domain;

import java.util.List;

/**
 * @moduleName: RequestTiles
 * @description: 地图瓦片批量下载请求参数
 * @author: 杨睿
 * @date: 2023/1/31 17:19
 **/
public class RequestTiles {
    private List<TilePosition> tilePositions;

    private List<String> types;

    public List<TilePosition> getTilePositions () {
        return tilePositions;
    }

    public void setTilePositions (List<TilePosition> tilePositions) {
        this.tilePositions = tilePositions;
    }

    public List<String> getTypes () {
        return types;
    }

    public void setTypes (List<String> types) {
        this.types = types;
    }
}
