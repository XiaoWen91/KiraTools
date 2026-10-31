package io.github.angbang852.manjiao.hook

import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Kiri 模块 Q2 逻辑移植：稳定 token、扫描、区域透明度 key、视频控件定义。
 * 只保留纯 View 逻辑，不依赖 Kiri 的配置/UI 类。
 */
object KiriQ2 {
    data class ControlDef(val key: String, val label: String, val idNames: List<String>)
    data class Elem(val token: String, val label: String, val x: Int, val y: Int, val w: Int, val h: Int)

    val controls: List<ControlDef> = listOf(
        ControlDef("danmaku", "弹幕按钮", listOf("danmakuContainer")),
        ControlDef("nickname", "昵称", listOf("username_group")),
        ControlDef("caption", "文案 / 标签", listOf("caption_scroll_container")),
        ControlDef("ai_tip", "AI提示 / 声明", listOf("slide_play_photo_disclaimer_container")),
        ControlDef("auto_play", "自动上滑", listOf("pad_slide_auto_play_icon", "pad_slide_auto_play_text")),
        ControlDef("avatar", "头像 / 关注", listOf("slide_play_right_follow")),
        ControlDef("like", "点赞", listOf("like_button")),
        ControlDef("comment", "评论", listOf("comment_button")),
        ControlDef("collect", "收藏", listOf("collect_button")),
        ControlDef("share", "分享", listOf("forward_button")),
        ControlDef("music", "音乐转盘", listOf("music_wheel_container")),
        ControlDef("related", "相关搜索 / 热榜", listOf(
            "general_entry_single_root_view", "tabs_panel_style", "bottom_navigation_bar_shadow",
            "bottom_shadow", "entrytextlayout", "featuredbottomentry", "generalbottomentrytextlayout",
            "bottomentrylayout", "bottombar_entry"
        )),
        ControlDef("progress", "进度条", listOf("nasa_milano_progress_container", "milano_player_seekbar")),
        ControlDef("top_tab", "顶栏 Tab（发现 / 精选）", listOf("left_title")),
        ControlDef("top_back", "顶栏 返回键", listOf("left_btn")),
        ControlDef("top_search", "顶栏 搜索键", listOf("search_btn")),
        ControlDef("top_camera", "顶栏 相机 / 拍摄", listOf("camera_btn_layout", "camera_btn")),
    )

    private val alphaMap: Map<String, String> = mapOf(
        "home_tab_bg" to "alpha_top_bar",
        "featured_milano_front_view" to "alpha_top_bar",
        "front_top_view" to "alpha_top_bar",
        "bottom_comment_layout" to "alpha_bottom_bar",
        "group_right_action_bar_root_layout" to "alpha_right_bar",
        "group_information_root_layout" to "alpha_left_info",
        "bottom_bar_container" to "alpha_bottom_bar",
        "bottom_bar_action_bar" to "alpha_bottom_bar",
        "layout_bottom_action_bar_parent" to "alpha_bottom_bar",
        "group_bottom_root_layout" to "alpha_bottom_bar",
        "bottom_bar_and_grey_cover_container" to "alpha_bottom_bar",
    )

    fun resName(v: View): String? {
        val id = v.id
        if (id == 0 || id == View.NO_ID) return null
        return try {
            v.resources.getResourceEntryName(id).takeIf { it.isNotBlank() }
        } catch (_: Throwable) {
            null
        }
    }

    /** Kiri Q2.d：id 优先；无 id 时用 cls:simple|t=text / |d=desc。 */
    fun tokenFor(v: View): String? {
        resName(v)?.let { return "id:$it" }
        val cls = v.javaClass.name
        if (cls.startsWith("android.") || cls.startsWith("androidx.")) return null
        val simple = cls.substringAfterLast('.').lowercase()
        if (simple.isEmpty()) return null
        val text = (v as? TextView)?.text?.toString()?.trim().orEmpty()
        val desc = v.contentDescription?.toString()?.trim().orEmpty()
        val suffix = when {
            text.isNotEmpty() -> "t=$text"
            desc.isNotEmpty() -> "d=$desc"
            else -> null
        }
        return if (suffix == null) "cls:$simple" else "cls:$simple|$suffix"
    }

    /** Kiri Q2.e：按 token 递归找 View。 */
    fun findViewByToken(root: View, token: String): View? {
        if (root.tag == "ks_module_overlay") return null
        if (tokenFor(root) == token) return root
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                findViewByToken(root.getChildAt(i), token)?.let { return it }
            }
        }
        return null
    }

    /** Kiri Q2.f：弹幕控件 id 判定。 */
    fun isDanmaku(v: View): Boolean {
        val name = resName(v) ?: return false
        return name == "group_danmaku_root_layout" ||
            name == "nasa_danmaku_view_container" ||
            name == "infinity_danmaku_view" ||
            name == "danmakuContainer"
    }

    /** Kiri Q2.j：区域透明度 key。返回 alpha_top_bar / alpha_right_bar / alpha_left_info / alpha_bottom_bar。 */
    fun regionKey(v: View): String? {
        val cls = v.javaClass.name.lowercase()
        val id = resName(v)?.lowercase().orEmpty()
        if (id.contains("comment") || id.contains("message") || cls.contains("comment") || cls.contains("message")) return null

        if (id == "title_root") {
            var p: View? = v
            var depth = 0
            while (p != null && depth <= 10) {
                val pid = resName(p)?.lowercase().orEmpty()
                if (pid == "search_root" || pid.startsWith("search_home_top")) return null
                p = p.parent as? View
                depth++
            }
            return "alpha_top_bar"
        }

        alphaMap[id]?.let { return it }
        if (cls.contains("actionbar") && !cls.contains("cubetabactionbar")) return "alpha_top_bar"
        return null
    }

    /** Kiri Q2.k + Q2.l：扫描可隐藏元素，面积不超过根 View 的 35%。 */
    fun scan(root: View): List<Elem> {
        val out = ArrayList<Elem>()
        val rootView = root.rootView ?: root
        val rootArea = (rootView.width.toLong().coerceAtLeast(1L)) * rootView.height.toLong().coerceAtLeast(1L)
        collect(root, rootArea, out, HashSet(), HashSet())
        return out
    }

    private fun collect(
        v: View,
        rootArea: Long,
        out: MutableList<Elem>,
        seenToken: MutableSet<String>,
        seenRect: MutableSet<String>,
    ) {
        if (v.tag == "ks_module_overlay") return
        if (v.isShown && v.width > 0 && v.height > 0) {
            val area100 = v.width.toLong() * v.height.toLong() * 100L
            if (area100 <= rootArea * 35L) {
                val token = tokenFor(v)
                if (token != null) {
                    val label = labelFor(v)
                    if (label.isNotEmpty()) {
                        val loc = IntArray(2)
                        v.getLocationOnScreen(loc)
                        val rect = "${loc[0] / 8},${loc[1] / 8},${v.width / 8},${v.height / 8}"
                        if (seenToken.add(token) && seenRect.add(rect)) {
                            out.add(Elem(token, label, loc[0], loc[1], v.width, v.height))
                        }
                    }
                }
            }
        }
        if (v is ViewGroup) {
            for (i in 0 until v.childCount) collect(v.getChildAt(i), rootArea, out, seenToken, seenRect)
        }
    }

    private fun labelFor(v: View): String {
        (v as? TextView)?.text?.toString()?.trim()?.takeIf { it.isNotEmpty() && it.length <= 12 }?.let { return it }
        v.contentDescription?.toString()?.trim()?.takeIf { it.isNotEmpty() && it.length <= 12 }?.let { return it }
        resName(v)?.let { return it }
        return v.javaClass.simpleName
    }
}
