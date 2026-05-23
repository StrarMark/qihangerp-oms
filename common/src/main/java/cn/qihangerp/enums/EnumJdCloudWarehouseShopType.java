package cn.qihangerp.enums;

/**
 * 描述：
 * 店铺类型Enum
 *
 * @author qlp
 * @date 2019-09-18 19:44
 */
public enum EnumJdCloudWarehouseShopType {
    JD("京东商城", 1),
    TMALL("天猫商城", 2),
    SNYG("苏宁易购", 3),
    YMX("亚马逊中国", 4),
    CK("ChinaSkin", 5),
//    QT("其他", 6),
    DANGDANG("当当网", 7),
    TAO("淘宝网", 8),
    PAIPAI("拍拍商城", 9),
    YHSC("1号商城", 10),
    VJ("V+", 11),
    MKL("麦考林", 12),
    ZXW("走秀网", 13),
    YTSC("易通商城", 14),
    LFW("乐蜂网", 15),
    YL("邮乐", 16),
    WPH("唯品会", 17),
    GOME("国美在线", 18),
    JMYP("聚美优品", 19),
    MLS("美丽说", 20),
    MGJ("蘑菇街", 21),
    YTW("银泰网", 22),
    B2B("B2B电商云", 23),
    JDDJ("京东到家", 24),
    XJ("鲜家", 25),
    OFFICIAL("official", 26),
    XHS("小红书", 27),
    POS("POS", 29),
    taobaoMall("taobaoMall", 30),
    jdv2("jdv2", 31),
    YIHAODIAN("YIHAODIAN", 32),
    taobaoFenXiao("taobaoFenXiao", 33),
    yougou("yougou", 34),
    DangDang("DangDang", 35),
    TIANHONG("TIANHONG", 36),
    FQLPT("分期乐平台", 38),
    XXSC("线下门店（门店商超等实体）", 43),
    XX("线下", 44),
    CCJ("楚楚街", 45),
    BBW("贝贝网", 46),
    RCKJPT("仓融科技平台", 47),
    PDD("拼多多", 48),
    ZBPT("直播平台（视频/小视频类/直播平台等）", 49),
    WXWSL("微信微商类", 50),
    YHLXSPT("银行类销售平台", 51),
    JRLPT("金融类平台", 52),
    FLPT("福利平台（积分兑/优惠券兑/抽奖等）", 53),
    SJZYGW("商家自营官网", 54),
    CZDSPT("垂直电商平台（美妆/母婴/奢侈品等）", 55),
    ZX("直销平台（电视/广播/纸媒/线上广告/线上批发）", 56),
    TG("团购平台（美团/糯米/饿了么等）", 57),
    DY("抖音", 58),
    KS("快手", 59),
    YMALL("云mall", 60),
    SL("商羚", 61),
    XDD("享东东", 62),
    QQDWJ("全渠道万家", 63),
    QQDZZ("全渠道主站", 64),
    DD("多点", 65),
    YHYX("云货优选", 66),
    YZ("有赞", 67),
    TXHJ("腾讯惠聚", 70),
    XEPP("小鹅拼拼", 71),
    DYDF("抖音代发", 72),
    ALBB("阿里巴巴", 73),
    WXSC("微信微商类", 74),
    WXXD("微信小店", 75),
    WPXJS("唯品会集市", 76),
    DW("得物", 77),
    DWZF("得物直发", 78),
    MJYP("米家有品", 1066),
    YFX("云分销", 1067),
    YMSC("云米商城", 1068),
    YMOA("云米OA系统", 1069),
    YMDRP("云米DRP系统", 1070),
    KJSH("科技-山海", 1071),
    XCHYSC("携程会员商城", 1072),
    WXXCX("微信小程序", 79),
    AKC("爱库存", 81),
    YHD("一号店", 92),
//    XHS("小红书", 84),
//    YZ("有赞", 123),
//    DY("抖音", 299),
    KSMING("快手（明文）", 8040355),
    KSMI("快手（密文）", 8040559),

    QT("其他", 6);
    private String name;
    private int index;

    // 构造方法
    private EnumJdCloudWarehouseShopType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (EnumJdCloudWarehouseShopType c : EnumJdCloudWarehouseShopType.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
