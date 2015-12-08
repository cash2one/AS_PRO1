package com.linkage.mobile72.sh.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.FaceListAdapter;
import com.linkage.mobile72.sh.widget.FacePanelView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;

public class FaceUtils {
	
//	private static final Integer[] FACE_IMAGE_IDS = { R.drawable.face_88_thumb,
//		R.drawable.face_angrya_thumb, R.drawable.face_bba_thumb,
//		R.drawable.face_bingo_thumb, R.drawable.face_bobotiaopi_thumb,
//		R.drawable.face_bs2_thumb, R.drawable.face_bs_thumb,
//		R.drawable.face_bz_thumb, R.drawable.face_cake,
//		R.drawable.face_chrishat_thumb, R.drawable.face_christree_thumb,
//		R.drawable.face_ciya_thumb, R.drawable.face_cj_thumb,
//		R.drawable.face_clock_thumb, R.drawable.face_come_thumb,
//		R.drawable.face_cool_thumb, R.drawable.face_crazya_thumb,
//		R.drawable.face_cry, R.drawable.face_cza_thumb,
//		R.drawable.face_dizzya_thumb, R.drawable.face_fuyun_thumb,
//		R.drawable.face_geili_thumb, R.drawable.face_good_thumb,
//		R.drawable.face_gza_thumb, R.drawable.face_hatea_thumb,
//		R.drawable.face_hearta_thumb, R.drawable.face_heia_thumb,
//		R.drawable.face_horse2_thumb, R.drawable.face_hsa_thumb,
//		R.drawable.face_hufen_thumb, R.drawable.face_h_thumb,
//		R.drawable.face_j_thumb, R.drawable.face_kbsa_thumb,
//		R.drawable.face_kl_thumb,
//		R.drawable.face_k_thumb, R.drawable.face_laugh,
//		R.drawable.face_lazu_thumb, R.drawable.face_ldln_thumb,
//		R.drawable.face_liwu_thumb, R.drawable.face_lovea_thumb,
//		R.drawable.face_lundunaohuo_thumb, R.drawable.face_mb_thumb,
//		R.drawable.face_money_thumb, R.drawable.face_m_thumb,
//		R.drawable.face_nm_thumb, R.drawable.face_no_thumb,
//		R.drawable.face_ok_thumb, R.drawable.face_otm_thumb,
//		R.drawable.face_panda_thumb, R.drawable.face_pig,
//		R.drawable.face_qq_thumb, R.drawable.face_rabbit_thumb,
//		R.drawable.face_sada_thumb, R.drawable.face_sad_thumb,
//		R.drawable.face_sb_thumb, R.drawable.face_shamea_thumb,
//		R.drawable.face_shoutao_thumb, R.drawable.face_sk_thumb,
//		R.drawable.face_sleepa_thumb, R.drawable.face_sleepya_thumb,
//		R.drawable.face_smilea_thumb, R.drawable.face_snow_thumb,
//		R.drawable.face_sweata_thumb, R.drawable.face_sw_thumb,
//		R.drawable.face_tootha_thumb, R.drawable.face_totoyouai_thumb,
//		R.drawable.face_tza_thumb, R.drawable.face_t_thumb,
//		R.drawable.face_unheart, R.drawable.face_vw_thumb,
//		R.drawable.face_weijin_thumb, R.drawable.face_wennuanmaozi_thumb,
//		R.drawable.face_wg_thumb, R.drawable.face_wq_thumb,
//		R.drawable.face_xx2_thumb, R.drawable.face_x_thumb,
//		R.drawable.face_yellowmood_thumb, R.drawable.face_ye_thumb,
//		R.drawable.face_yhh_thumb, R.drawable.face_yw_thumb,
//		R.drawable.face_yx_thumb, R.drawable.face_y_thumb,
//		R.drawable.face_z2_thumb, R.drawable.face_zhh_thumb,
//		R.drawable.face_zy_thumb, R.drawable.face_zz2_thumb };
	
//	private static final String[] FACE_TEXTS = { "[拜拜]", "[怒]", "[抱抱]",
//		"[礼花]", "[淘气]", "[鄙视]", "[悲伤]", "[闭嘴]", "[蛋糕]", "[圣诞帽]", "[圣诞树]",
//		"[呰牙]", "[吃惊]", "[钟]", "[来]", "[酷]", "[抓狂]", "[衰]", "[馋嘴]", "[晕]",
//		"[浮云]", "[给力]", "[good]", "[鼓掌]", "[哼]", "[心]", "[偷笑]", "[神马]",
//		"[花心]", "[互粉]", "[黑线]", "[囧]", "[抠鼻]", "[可怜]", "[打哈欠]",
//		"[哈哈]", "[蜡烛]", "[懒得理你]", "[礼物]", "[爱你]", "[火炬]", "[太开心]", "[钱]",
//		"[话筒]", "[狂骂]", "[不要]", "[ok]", "[奥特曼]", "[熊猫]", "[猪头]", "[亲亲]",
//		"[兔子]", "[泪]", "[弱]", "[生病]", "[害羞]", "[手套]", "[思考]", "[睡觉]",
//		"[困]", "[呵呵]", "[雪花]", "[汗]", "[失望]", "[嘻嘻]", "[有爱]", "[可爱]",
//		"[吐]", "[伤心]", "[威武]", "[围脖]", "[温暖帽子]", "[围观]", "[委屈]", "[雪人]",
//		"[嘘]", "[落叶]", "[耶]", "[右哼哼]", "[疑问]", "[阴险]", "[药]", "[赞]",
//		"[左哼哼]", "[挤眼]", "[织]" };
	
	private static final Integer[] FACE_IMAGE_IDS = { 
		R.drawable.smile,R.drawable.tooth,R.drawable.laugh,R.drawable.love
		,R.drawable.dizzy,R.drawable.sad,R.drawable.cz_thumb,R.drawable.crazy
		,R.drawable.hate,R.drawable.bb_thumb,R.drawable.tz_thumb,R.drawable.angry
		,R.drawable.sweat,R.drawable.sleepy,R.drawable.shame_thumb,R.drawable.sleep_thumb
		,R.drawable.money_thumb,R.drawable.hei_thumb,R.drawable.cool_thumb,R.drawable.cry
		,R.drawable.cj_thumb,R.drawable.bz_thumb,R.drawable.bs2_thumb,R.drawable.kbs_thumb,R.drawable.hs_thumb
		,R.drawable.gz_thumb,R.drawable.sw_thumb,R.drawable.sk_thumb,R.drawable.sb_thumb,R.drawable.qq_thumb
		,R.drawable.nm_thumb,R.drawable.mb_thumb,R.drawable.ldln_thumb,R.drawable.yhh_thumb,R.drawable.zhh_thumb
		,R.drawable.x_thumb,R.drawable.wq_thumb,R.drawable.t_thumb,R.drawable.kl_thumb,R.drawable.k_thumb
		,R.drawable.d_thumb,R.drawable.yw_thumb,R.drawable.zgl_thumb,R.drawable.ws_thumb,R.drawable.ye_thumb
		,R.drawable.good_thumb,R.drawable.sad_thumb,R.drawable.no_thumb,R.drawable.ok_thumb,R.drawable.z2_thumb
		,R.drawable.come_thumb,R.drawable.cake,R.drawable.heart,R.drawable.unheart,R.drawable.clock_thumb
		,R.drawable.pig,R.drawable.m_thumb,R.drawable.moon,R.drawable.sun,R.drawable.rain,R.drawable.zhi
		,R.drawable.shenma,R.drawable.fuyun,R.drawable.geili,R.drawable.weiguan,R.drawable.weiwu,R.drawable.panda
		,R.drawable.rabbit,R.drawable.otm,R.drawable.j_org,R.drawable.hufen_org,R.drawable.liwu_org,R.drawable.yellow_org
		,R.drawable.wennuanmaozi_org,R.drawable.shoutao_org,R.drawable.weijin_org,R.drawable.bingo_org,R.drawable.xi_org
		,R.drawable.bobotiaopi_org,R.drawable.totoyouai_org,R.drawable.ciya_org,R.drawable.kelian_org,R.drawable.shoufa_org
		,R.drawable.yinyuehe_org, R.drawable.lazu_org
	};
	private static final String[] FACE_TEXTS = {
		"(#呵呵)", "(#嘻嘻)", "(#哈哈)", "(#爱你)", "(#晕)", "(#泪)",
		"(#馋嘴)", "(#抓狂)", "(#哼)", "(#抱抱)", "(#可爱)", "(#怒)", "(#汗)", "(#困)", "(#害羞)",
		"(#睡觉)", "(#钱)", "(#偷笑)", "(#酷)", "(#衰)", "(#吃惊)", "(#闭嘴)", "(#鄙视)",
		"(#挖鼻屎)", "(#花心)", "(#鼓掌)", "(#失望)", "(#思考)", "(#生病)", "(#亲亲)", "(#怒骂)",
		"(#太开心)", "(#懒得理你)", "(#右哼哼)", "(#左哼哼)", "(#嘘)", "(#委屈)", "(#吐)", "(#可怜)",
		"(#打哈气)", "(#顶)", "(#疑问)", "(#做鬼脸)", "(#握手)", "(#耶)", "(#强)", "(#弱)",
		"(#不要)", "(#好的)", "(#赞)", "(#来)", "(#蛋糕)", "(#心)", "(#伤心)", "(#钟)", "(#猪头)",
		"(#话筒)", "(#月亮)", "(#太阳)", "(#下雨)","(#织)","(#神马)","(#浮云)","(#给力)","(#围观)",
		"(#威武)","(#熊猫)","(#兔子)","(#奥特曼)","(#囧)","(#互粉)","(#礼物)","(#落叶)","(#温暖帽子)",
		"(#手套)","(#围脖)","(#礼花)","(#喜)","(#淘气)","(#有爱)","(#好可怜)","(#呲牙)","(#首发)",
		"(#音乐盒)","(#蜡烛)"
	};
	
	public static List<Face> FACES = new ArrayList<Face>();
	public static Pattern PATTERN_FACE = Pattern.compile("(\\(#\\w+\\))");
	public static Map<String, Integer> FACES_MAP = new HashMap<String, Integer>();
	
	static {
		for(int i = 0; i < FACE_IMAGE_IDS.length; i ++) {
			Face face = new Face(FACE_TEXTS[i], FACE_IMAGE_IDS[i]);
			FACES.add(face);
			FACES_MAP.put(FACE_TEXTS[i], FACE_IMAGE_IDS[i]);
		}
	}
	
	public static SpannableString replaceFace(Context context, String text) {
		SpannableString spannableString;
		if(text == null) {
			text = "";
			spannableString = new SpannableString(text);
			return spannableString;
		}
		spannableString = new SpannableString(text);
		
		Matcher matcher = PATTERN_FACE.matcher(text);
		while(matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			Integer integer = FACES_MAP.get(matcher.group());
			if(integer != null) {
				spannableString.setSpan(getImageSpan(context, integer), 
						start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			}
		}
		
		return spannableString;
	}
	
	public static ImageSpan getImageSpan(Context context, int id) {
		Drawable drawable = context.getResources().getDrawable(id);
		
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width + 4, height + 4, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(2, 2, width + 2, height + 2);
		drawable.draw(canvas);
		
		ImageSpan span = new ImageSpan(context, bitmap, ImageSpan.ALIGN_BASELINE);
		return span;
	}
	
	public static void install(Activity activity, FacePanelView.OnFaceClickListener listener) {
		FacePanelView facePanel = (FacePanelView) activity.findViewById(R.id.face_panel);
		if(facePanel == null) {
			return;
		}
		facePanel.setAdapter(new FaceListAdapter(activity, FACES));
		facePanel.setOnFaceClickListener(listener);
	}
	
	public static void toggleFacePanel(Activity activity) {
		View panel = activity.findViewById(R.id.face_panel);
		if(panel == null) {
			return;
		}
		if(panel.getVisibility() == View.VISIBLE) {
			panel.setVisibility(View.GONE);
		} else {
			panel.setVisibility(View.VISIBLE);
		}
	}
	
	public static class Face {
		public final String text;
		public final int imageId;
		
		public Face(String text, int imageId) {
			this.text = text;
			this.imageId = imageId;
		}
	}
}
