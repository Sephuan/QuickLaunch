package com.sephuan.quicklaunch.data

object DefaultApps {
    const val CAT_SOCIAL = "社交"
    const val CAT_PAYMENT = "支付"
    const val CAT_VIDEO = "视频"
    const val CAT_SHOPPING = "购物"
    const val CAT_GAME = "游戏"
    const val CAT_TOOL = "工具"
    const val CAT_MUSIC = "音乐"
    const val CAT_NEWS = "资讯"

    val mappings: Map<String, List<String>> = mapOf(
        "com.tencent.mm" to listOf(CAT_SOCIAL, CAT_PAYMENT),
        "com.tencent.mobileqq" to listOf(CAT_SOCIAL),
        "com.sina.weibo" to listOf(CAT_SOCIAL, CAT_NEWS),
        "com.zhihu.android" to listOf(CAT_SOCIAL, CAT_NEWS),

        "com.eg.android.AlipayGphone" to listOf(CAT_PAYMENT, CAT_TOOL),
        "com.sankuai.meituan" to listOf(CAT_PAYMENT, CAT_TOOL),
        "me.ele" to listOf(CAT_PAYMENT, CAT_TOOL),

        "com.ss.android.ugc.aweme" to listOf(CAT_VIDEO, CAT_SOCIAL),
        "com.smile.gifmaker" to listOf(CAT_VIDEO, CAT_SOCIAL),
        "tv.danmaku.bili" to listOf(CAT_VIDEO, CAT_GAME),
        "com.tencent.qqlive" to listOf(CAT_VIDEO),
        "com.qiyi.video" to listOf(CAT_VIDEO),
        "com.youku.phone" to listOf(CAT_VIDEO),

        "com.taobao.taobao" to listOf(CAT_SHOPPING),
        "com.jingdong.app.mall" to listOf(CAT_SHOPPING),
        "com.xunmeng.pinduoduo" to listOf(CAT_SHOPPING),

        "com.netease.cloudmusic" to listOf(CAT_MUSIC),
        "com.tencent.qqmusic" to listOf(CAT_MUSIC),

        "com.tencent.tmgp.sgame" to listOf(CAT_GAME, CAT_SOCIAL),
        "com.tencent.tmgp.pubgmhd" to listOf(CAT_GAME),
        "com.miHoYo.Yuanshen" to listOf(CAT_GAME)
    )
}
