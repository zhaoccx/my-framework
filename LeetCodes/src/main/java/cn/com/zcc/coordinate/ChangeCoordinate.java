package cn.com.zcc.coordinate;

/**
 * @title: ChangeCoordinate
 * @projectName my-framework
 * @Description 提供了百度坐标（BD09）、国测局坐标（火星坐标，GCJ02）、和WGS84坐标系之间的转换
 * @Author zhaocc
 * @DateTime 2019-7-20 8:50
 */
public class ChangeCoordinate {
    //定义一些常量
    private final double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
    private final double PI = 3.1415926535897932384626;
    private final double a = 6378245.0;
    private final double ee = 0.00669342162296594323;

    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     * 即 百度 转 谷歌、高德
     *
     * @param bd_lon
     * @param bd_lat
     * @return
     */
    public double[] bd09togcj02(double bd_lon, double bd_lat) {
//        var x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new double[]{gg_lng, gg_lat};
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     *
     * @param lng
     * @param lat
     * @return
     */
    public double[] gcj02tobd09(double lng, double lat) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new double[]{bd_lng, bd_lat};
    }

    /**
     * WGS84转GCj02
     * @param lng
     * @param lat
     * @return
     */
    public double[] wgs84togcj02(double lng, double lat) {
        if (out_of_china(lng, lat)) {
            return new double[]{lng, lat};
        } else {
            double dlat = transformlat(lng - 105.0, lat - 35.0);
            double dlng = transformlng(lng - 105.0, lat - 35.0);
            double radlat = lat / 180.0 * PI;
            double magic = Math.sin(radlat);
            magic = 1 - ee * magic * magic;
            double sqrtmagic = Math.sqrt(magic);
            dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
            dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
            double mglat = lat + dlat;
            double mglng = lng + dlng;
            return new double[]{mglng, mglat};
        }
    }


    /**
     * GCJ02 转换为 WGS84
     * @param lng
     * @param lat
     * @return
     */
    public double[] gcj02towgs84(double lng,double lat) {
        if (out_of_china(lng, lat)) {
            return new double[] {lng, lat};
        }
        else {
            double dlat = transformlat(lng - 105.0, lat - 35.0);
            double dlng = transformlng(lng - 105.0, lat - 35.0);
            double radlat = lat / 180.0 * PI;
            double magic = Math.sin(radlat);
            magic = 1 - ee * magic * magic;
            double sqrtmagic = Math.sqrt(magic);
            dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
            dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
            double  mglat = lat + dlat;
            double mglng = lng + dlng;
            return new double[]{lng * 2 - mglng, lat * 2 - mglat};
        }
    }


    private double transformlat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private double transformlng(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

     private boolean out_of_china(double lng,double lat) {
        return (lng < 72.004 || lng > 137.8347) || ((lat < 0.8293 || lat > 55.8271) || false);
    }


    public static void main(String[] args) {
        ChangeCoordinate coordinate = new ChangeCoordinate();
        double[] togcj02 = coordinate.wgs84togcj02(113.9456981, 22.75328226);
        System.out.println(togcj02[0]+","+togcj02[1]);
        double[] baidu = coordinate.gcj02tobd09(togcj02[0],togcj02[1]);
        System.out.println(baidu[0]+","+baidu[1]);
//        System.out.println(baidu[1]);

    }
}