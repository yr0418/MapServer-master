package com.cheese.MapServer.utils;

import com.cheese.MapServer.bean.LatLngInfo;
import com.cheese.MapServer.bean.LevelInfo;
import com.cheese.MapServer.bean.ThreadReqParamInfo;
import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class InitUtils {
    // 待匹配的URL
    private static String Google_Satellite_Url = "http://mt0.google.cn/vt/lyrs=y&hl=zh-CN&hl=zh-CN&gl=CN&x={x}&y={y}&z={z}&s=Gali";
    private static String Google_Image_Url = "http://mt0.google.cn/vt/lyrs=m&hl=zh-CN&hl=zh-CN&gl=CN&x={x}&y={y}&z={z}&s=Gali";
    private static String Google_Terrain_Url = "http://mt0.google.cn/vt/lyrs=p&hl=zh-CN&hl=zh-CN&gl=CN&x={x}&y={y}&z={z}&s=Gali";
    private static String AMap_Satellite_Url = "http://webst02.is.autonavi.com/appmaptile?style=6&x={x}&y={y}&z={z}";
    private static String AMap_Cover_Url = "http://webst02.is.autonavi.com/appmaptile?x={x}&y={y}&z={z}&lang=zh_cn&size=1&scale=1&style=8";
    private static String AMap_Image_Url = "http://webrd03.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}";
    private static String TianDiTu_Satellite_Url = "http://t1.tianditu.cn/DataServer?T=img_w&X={x}&Y={y}&L={z}";
    private static String TianDiTu_Image_Url = "http://t1.tianditu.com/DataServer?T=vec_w&tk=9fd159d68457d255695a43c78e45d450&x={x}&y={y}&l={z}";
    private static String TianDiTu_Cover_Url = "http://t2.tianditu.com/DataServer?T=cva_w&tk=9fd159d68457d255695a43c78e45d450&x={x}&y={y}&l={z}";

    public static Boolean getPic (BackgroundType type, Integer x, Integer y, Integer z) {
        try {
            File saveImg = new File(getPathByType(type) + z + "/" + x + "/" + y + "/img.png");
            String url = buildUrl(x, y, z, type);
            BufferedImage image = HttpUtils.getImage(url);

            if (saveImg.exists()) {
                return true;
            }

            File xF = new File(getPathByType(type) + z);
            if (! xF.exists()) {
                xF.mkdir();
            }
            File xyF = new File(getPathByType(type) + z + "/" + x);
            if (! xyF.exists()) {
                xyF.mkdir();
            }
            File xyzF = new File(getPathByType(type) + z + "/" + x + "/" + y);
            if (! xyzF.exists()) {
                xyzF.mkdir();
            }

            ImageIO.write(image, "png", saveImg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String buildUrl (Integer x, Integer y, Integer z, BackgroundType type) {
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

        url = url.replace("{x}", x + "");
        url = url.replace("{y}", y + "");
        url = url.replace("{z}", z + "");

        return url;
    }

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
                result = "map/tianditu/image/";
                break;
            default:
                result = "map/tianditu/image/";
                break;
        }
        return result;
    }

    public static BackgroundType getTypeByName (String typeName) {
        BackgroundType type = BackgroundType.Google_Satellite;

        switch (typeName) {
            case "google-satellite":
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
            case "tianditu-image":
                type = BackgroundType.TianDiTu_Image;
                break;
            case "tianditu-cover":
                type = BackgroundType.TianDiTu_Cover;
                break;
            default:
                type = BackgroundType.TianDiTu_Image;
                break;
        }

        return type;
    }
}
