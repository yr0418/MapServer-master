package com.yr.MapServer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class InitUtils {

    private static final Logger log = LoggerFactory.getLogger(InitUtils.class);
    @Value("${map-server.tiandituKeys}")
    private String tiandituKeys;

    private String[] tiandituKeysList;

    // 待匹配的URL
    private static String Google_Satellite_Url = "http://mt0.google.cn/vt/lyrs=y&hl=zh-CN&hl=zh-CN&gl=CN&x={x}&y={y}&z={z}&s=Gali";
    private static String Google_Image_Url = "http://mt0.google.cn/vt/lyrs=m&hl=zh-CN&hl=zh-CN&gl=CN&x={x}&y={y}&z={z}&s=Gali";
    private static String Google_Terrain_Url = "http://mt0.google.cn/vt/lyrs=p&hl=zh-CN&hl=zh-CN&gl=CN&x={x}&y={y}&z={z}&s=Gali";
    private static String AMap_Satellite_Url = "http://webst02.is.autonavi.com/appmaptile?style=6&x={x}&y={y}&z={z}";
    private static String AMap_Cover_Url = "http://webst02.is.autonavi.com/appmaptile?x={x}&y={y}&z={z}&lang=zh_cn&size=1&scale=1&style=8";
    private static String AMap_Image_Url = "http://webrd03.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}";
    private static String TianDiTu_Satellite_Url = "http://t1.tianditu.cn/DataServer?T=img_w&X={x}&Y={y}&L={z}";
    private static String TianDiTu_Image_Url = "http://t{index}.tianditu.gov.cn/vec_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk={token}";

    String vec = "http://t{index}.tianditu.com/DataServer?T=vec_w&tk={token}&x={x}&y={y}&l={z}";

    String cva = "http://t{index}.tianditu.com/DataServer?T=cva_w&tk={token}&x={x}&y={y}&l={z}";
    private static String TianDiTu_Cover_Url = "http://t{index}.tianditu.gov.cn/cva_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cva&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk={token}";

    /**
     * 保存地图瓦片至本地
     *
     * @param type 瓦片类型
     * @param x    瓦片 x 坐标
     * @param y    瓦片 y 坐标
     * @param z    瓦片 z 坐标
     *
     * @return 保存成功与否
     */
    public Boolean saveImage (BackgroundType type, Integer x, Integer y, Integer z) {
        try {
            File saveImg = new File(getPathByType(type) + z + "/" + x + "/" + y + "/img.png");
            if (saveImg.exists()) {
                return true;
            }
            // 构造瓦片请求路径，请求瓦片
            String url = buildUrl(x, y, z, type);
            BufferedImage image = HttpUtils.getImage(url);
            if (image == null) {
                return false;
            }
            // 若对应存储目录不存在，创建目录
            File xF = new File(getPathByType(type) + z);
            if (! xF.exists()) {
                xF.mkdirs();
            }
            File xyF = new File(getPathByType(type) + z + "/" + x);
            if (! xyF.exists()) {
                xyF.mkdirs();
            }
            File xyzF = new File(getPathByType(type) + z + "/" + x + "/" + y);
            if (! xyzF.exists()) {
                xyzF.mkdirs();
            }
            ImageIO.write(image, "png", saveImg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取地图瓦片，不进行本地保存
     *
     * @param type 地图瓦片类型
     * @param x    瓦片 x 坐标
     * @param y    瓦片 y 坐标
     * @param z    瓦片级别
     *
     * @return 地图瓦片二进制数组
     */
    public byte[] getImage(BackgroundType type, Integer x, Integer y, Integer z) {
        try {
            File saveImg = new File(getPathByType(type) + z + "/" + x + "/" + y + "/img.png");
            if (saveImg.exists()) {
                FileInputStream inputStream = new FileInputStream(saveImg);
                byte[] data = new byte[(int) saveImg.length()];
                inputStream.read(data);
                inputStream.close();
                return data;
            } else {
                String url = buildUrl(x, y, z, type);
                BufferedImage image = HttpUtils.getImage(url);
                if (image == null) {
                    return new byte[0];
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", bos);
                byte[] data = bos.toByteArray();
                bos.close();
                return data;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * 构造瓦片地图请求路径
     *
     * @param x    地图瓦片 x 坐标
     * @param y    地图瓦片 y 坐标
     * @param z    地图瓦片级别
     * @param type 地图瓦片类型
     *
     * @return 地图瓦片请求地址
     */
    private String buildUrl (Integer x, Integer y, Integer z, BackgroundType type) {
        String url = "";
        switch (type) {
            case Google_Satellite:
                url = Google_Satellite_Url;
                break;
            case Google_Image:
                url = Google_Image_Url;
                break;
            case Google_Terrain:
                url = Google_Terrain_Url;
                break;
            case AMap_Image:
                url = AMap_Image_Url;
                break;
            case AMap_Satellite:
                url = AMap_Satellite_Url;
                break;
            case AMap_Cover:
                url = AMap_Cover_Url;
                break;
            case TianDiTu_Image:
                url = TianDiTu_Image_Url;
                break;
            case TianDiTu_Cover:
                url = TianDiTu_Cover_Url;
                break;
            case TianDiTu_Satellite:
                url = TianDiTu_Satellite_Url;
                break;
            default:
                url = TianDiTu_Image_Url;
                break;
        }

        // 天地图的特殊处理
        if (url.contains("{index}")) {
            tiandituKeysList = tiandituKeys.split(",");
            int index = (int) Math.floor(Math.random() * 8);
            int token = (int)Math.floor(Math.random() * tiandituKeysList.length);
            url = url.replace("{index}", String.valueOf(index));
            url = url.replace("{token}", tiandituKeysList[token]);
        }
        url = url.replace("{x}", x + "");
        url = url.replace("{y}", y + "");
        url = url.replace("{z}", z + "");
        log.info("瓦片请求：{类型: " + type + ", z: " + z +", x: " + x + ", y: " + y + "}");
        return url;
    }

    /**
     * 获取地图瓦片的存储路径
     *
     * @param type 地图瓦片类型
     *
     * @return 存储路径
     */
    public static String getPathByType (BackgroundType type) {
        String result = "";
        switch (type) {
            case AMap_Cover:
                result = "map/amap/cover/";
                break;
            case AMap_Image:
                result = "map/amap/image/";
                break;
            case AMap_Satellite:
                result = "map/amap/satellite/";
                break;
            case Google_Image:
                result = "map/google/image/";
                break;
            case Google_Satellite:
                result = "map/google/satellite/";
                break;
            case Google_Terrain:
                result = "map/google/terrain/";
                break;
            case TianDiTu_Cover:
                result = "map/tianditu/cover/";
                break;
            case TianDiTu_Satellite:
                result = "map/tianditu/satellite/";
                break;
            case TianDiTu_Image:
            default:
                result = "map/tianditu/image/";
                break;
        }
        return result;
    }

    /**
     * 根据地图瓦片类型标识字段获取地图瓦片类型
     *
     * @param typeName 地图瓦片表示字段
     *
     * @return 地图瓦片类型
     */
    public static BackgroundType getTypeByName (String typeName) {
        BackgroundType type = BackgroundType.TianDiTu_Image;
        switch (typeName) {
            case "google-satellite":
                type = BackgroundType.Google_Satellite;
                break;
            case "google-image":
                type = BackgroundType.Google_Image;
                break;
            case "google-terrain":
                type = BackgroundType.Google_Terrain;
                break;
            case "amap-satellite":
                type = BackgroundType.AMap_Satellite;
                break;
            case "amap-image":
                type = BackgroundType.AMap_Image;
                break;
            case "amap-cover":
                type = BackgroundType.AMap_Cover;
                break;
            case "tianditu-satellite":
                type = BackgroundType.TianDiTu_Satellite;
                break;
            case "tianditu-cover":
                type = BackgroundType.TianDiTu_Cover;
                break;
            case "tianditu-image":
            default:
                break;
        }
        return type;
    }
}
