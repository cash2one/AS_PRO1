package com.linkage.mobile72.sh.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xclcharts.chart.PieData;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.KaoqinActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AttenPicState;
import com.linkage.mobile72.sh.data.StudentAtten;
import com.linkage.mobile72.sh.data.StudentAttenSum;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.widget.DountChart01View;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.CustomDialog;

public class ClassAttenAdapter extends BaseAdapter {
	private String TAG = ClassAttenAdapter.class.getSimpleName();

	private final int NUM_PER_LINE = 3;

	private LayoutInflater mLayoutInflater;

	private List<StudentAtten> stuAttenList = new ArrayList<StudentAtten>();

	private StudentAttenSum attenSum;

	// 确认考勤用
	private long classId = -1;

	private String uploadImgUrl = "";

	private String mDate;

	private CustomDialog dialog;

	private MyCommonDialog mDialog;
	private Activity activity;
	private int ret;

	private Handler handler;

	private AttenPicState mPicState;

	// public ClassAttenAdapter(Context context, List<StudentAtten> list,
	// StudentAttenSum sum)
	// public ClassAttenAdapter(Activity activity, List<StudentAtten> list)
	// {
	// this.stuAttenList = list;
	// this.activity = activity;
	// this.mLayoutInflater = LayoutInflater.from(activity);
	// }

	public ClassAttenAdapter(Activity activity, List<StudentAtten> list,
			Handler hdl) {
		this.stuAttenList = list;
		this.activity = activity;
		this.mLayoutInflater = LayoutInflater.from(activity);
		this.handler = hdl;
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	@Override
	public int getCount() {
		int count = stuAttenList.size() / NUM_PER_LINE + 1;

		if (0 != stuAttenList.size() % NUM_PER_LINE) {
			count += 1;
		}

		return count;
	}

	@Override
	public Object getItem(int position) {
		if (0 == position) {
			return attenSum;
		} else {
			return stuAttenList.get(position - 1);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		// LogUtils.e(TAG, " getview position=" + position);

		if (null == convertView) {
			convertView = mLayoutInflater.inflate(R.layout.atten_item, parent,
					false);

			holder = new ViewHolder();

			holder.lyAttenSum = (LinearLayout) convertView
					.findViewById(R.id.lyAttenSum);

			holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			holder.tvTotal = (TextView) convertView.findViewById(R.id.tvTotal);
			holder.tvLeave = (TextView) convertView.findViewById(R.id.tvLeave);
			holder.tvNotAtten = (TextView) convertView
					.findViewById(R.id.tvNotAtten);

			holder.btnConfirm = (Button) convertView
					.findViewById(R.id.btnConfirm);
			holder.btnHistory = (Button) convertView
					.findViewById(R.id.btnHistory);

			holder.lyAtten = (LinearLayout) convertView
					.findViewById(R.id.lyAtten);

			holder.rlyStudent1 = (RelativeLayout) convertView
					.findViewById(R.id.rlyStudent1);
			holder.rlyStudent2 = (RelativeLayout) convertView
					.findViewById(R.id.rlyStudent2);
			holder.rlyStudent3 = (RelativeLayout) convertView
					.findViewById(R.id.rlyStudent3);

			holder.tvStudent1 = (TextView) convertView
					.findViewById(R.id.tvStudent1);
			holder.tvStudent2 = (TextView) convertView
					.findViewById(R.id.tvStudent2);
			holder.tvStudent3 = (TextView) convertView
					.findViewById(R.id.tvStudent3);

			holder.imgv1 = (ImageView) convertView.findViewById(R.id.imgv1);
			holder.imgv2 = (ImageView) convertView.findViewById(R.id.imgv2);
			holder.imgv3 = (ImageView) convertView.findViewById(R.id.imgv3);

			holder.dountChart = (DountChart01View) convertView
					.findViewById(R.id.dountChart);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (0 == position) {
			holder.lyAttenSum.setVisibility(View.VISIBLE);
			holder.lyAtten.setVisibility(View.GONE);

			holder.tvDate.setText(attenSum.getDate());
			holder.tvTotal.setText(attenSum.getStuCount() + "");
			holder.tvLeave.setText(attenSum.getAskLeave() + "");
			holder.tvNotAtten.setText(attenSum.getLeave() + "");

			// Log.d(TAG, "getStuCount:" + attenSum.getStuCount() + " askLeave:"
			// + attenSum.getAskLeave() + " leave:" + attenSum.getLeave());

			if (attenSum.getStuCount() > 0) {
				float askLeavePers = (float) (attenSum.getAskLeave())
						/ (float) attenSum.getStuCount() * 100;
				float leavePers = (float) (attenSum.getLeave())
						/ (float) attenSum.getStuCount() * 100;

				// Log.d(TAG, "normalPers:" + (100 - askLeavePers - leavePers)
				// + " askLeavePers:" + askLeavePers + " leavePers:"
				// + leavePers);

				LinkedList<PieData> lPieData = new LinkedList<PieData>();
				lPieData.add(new PieData("", "",
						(100 - askLeavePers - leavePers), Color.rgb(126, 210,
								21)));
				lPieData.add(new PieData("", "", askLeavePers, Color.rgb(236,
						105, 65)));
				lPieData.add(new PieData("", "", leavePers, Color.rgb(247, 181,
						81)));

				holder.dountChart.updateDount(lPieData);
			} else {

			}

			if (getRet() == 0) {
				holder.btnConfirm
						.setBackgroundResource(R.drawable.atten_confirm_selector);
				holder.btnConfirm.setText("确认考勤");
				holder.btnConfirm.setEnabled(true);
				holder.btnConfirm.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Log.d(TAG, "btnConfirm on Clicked");

						mDialog = new MyCommonDialog(activity, "提示消息",
								"确认提交吗？", "取消", "确认");
						mDialog.setOkListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (-1 == classId) {
									Log.d(TAG,
											"canot confirm, invalid classId = "
													+ classId);
								} else {
									LogUtils.d("adatper, istaken:"
											+ mPicState.isTakeAttenPhoto());
									LogUtils.d("adatper, --------------------->istaken:"
											+ mPicState.isTakeAttenPhoto());
									
									if (mPicState.isTakeAttenPhoto()) {
										
										if (StringUtils.isEmpty(uploadImgUrl)) {

											if (null != handler) {
												Message message = new Message();
												message.what = 1;
												handler.sendMessage(message);

											} else {
												LogUtils.d("handler is null!!");
												Toast.makeText(activity,
														"确认考勤失败",
														Toast.LENGTH_SHORT)
														.show();
											}

										} else {
											LogUtils.d("adatper, --------------------->1");
											uploadClassAtten(classId);
										}
									} else {
										LogUtils.d("adatper, --------------------->2");
										uploadClassAtten(classId);
									}

								}

								mDialog.dismiss();
							}
						});
						mDialog.setCancelListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if (mDialog.isShowing())
									mDialog.dismiss();
							}
						});
						mDialog.show();

					}
				});
			} else {
				holder.btnConfirm
						.setBackgroundResource(R.drawable.atten_history_selector);
				holder.btnConfirm.setText("已考勤");
				holder.btnConfirm.setEnabled(false);
			}
			holder.btnHistory.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d(TAG, "btnHistory on Clicked");
					Intent intentAtten = new Intent(activity,
							KaoqinActivity.class);
					activity.startActivity(intentAtten);
					activity.finish();
				}
			});
		} else {
			holder.lyAttenSum.setVisibility(View.GONE);
			holder.lyAtten.setVisibility(View.VISIBLE);

			StudentAtten stuAtten;
			int strtPos = (position - 1) * NUM_PER_LINE;

			int size = stuAttenList.size();

			// LogUtils.e(TAG, " strtPos=" + strtPos + " size=" + size
			// + " size/NUM_PER_LINE=" + size / NUM_PER_LINE);

			for (int i = strtPos; i < (strtPos + 3) && i < size; i++) {
				stuAtten = stuAttenList.get(i);
				// LogUtils.e(TAG,
				// " i=" + i + "  i % NUM_PER_LINE=" + i % NUM_PER_LINE
				// + " name=" + stuAtten.getName() + " status="
				// + stuAtten.getState());

				switch (i % NUM_PER_LINE) {
				case 0:
					holder.tvStudent1.setText(stuAtten.getName());
					if (getRet() == 0) {
						holder.rlyStudent1
								.setOnClickListener(new StudentOnClickListener(
										i, holder.tvStudent1));
					} else {
						holder.rlyStudent1.setOnClickListener(null);
					}
					// if (2 == stuAtten.getState())
					// {
					// holder.imgv1.setVisibility(View.VISIBLE);
					// holder.imgv1.setImageResource(R.drawable.atten_ask_leave);
					// }
					// else
					// {
					// holder.imgv1.setVisibility(View.GONE);
					// }
					dspStateImg(holder.imgv1, stuAtten.getState());

					break;
				case 1:
					holder.tvStudent2.setText(stuAtten.getName());
					if (getRet() == 0) {
						holder.rlyStudent2
								.setOnClickListener(new StudentOnClickListener(
										i, holder.tvStudent2));
					} else {
						holder.rlyStudent1.setOnClickListener(null);
					}
					dspStateImg(holder.imgv2, stuAtten.getState());

					break;
				case 2:
					holder.tvStudent3.setText(stuAtten.getName());
					if (getRet() == 0) {
						holder.rlyStudent3
								.setOnClickListener(new StudentOnClickListener(
										i, holder.tvStudent3));
					} else {
						holder.rlyStudent1.setOnClickListener(null);
					}
					dspStateImg(holder.imgv3, stuAtten.getState());

					break;
				}
			}

			// last line
			if (position - 1 == size / NUM_PER_LINE) {
				int blank = NUM_PER_LINE - (size % NUM_PER_LINE);

				switch (blank) {
				case 0:
					break;
				case 1:
					holder.tvStudent3.setText("");
					holder.imgv3.setVisibility(View.GONE);
					break;
				case 2:
					holder.tvStudent2.setText("");
					holder.tvStudent3.setText("");
					holder.imgv2.setVisibility(View.GONE);
					holder.imgv3.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}

		}

		return convertView;
	}

	private void dspStateImg(ImageView imgv, int state) {
		if (StudentAtten.ATTEN_ASK_FOR_LEAVE == state) {
			imgv.setVisibility(View.VISIBLE);
			imgv.setImageResource(R.drawable.atten_ask_leave);
		} else if (StudentAtten.ATTEN_LEAVE == state) {
			imgv.setVisibility(View.VISIBLE);
			imgv.setImageResource(R.drawable.atten_leave);
		} else if (StudentAtten.ATTEN_NORMAL == state) {
			imgv.setVisibility(View.VISIBLE);
			imgv.setImageResource(R.drawable.atten_normal);
		} else {
			imgv.setVisibility(View.GONE);
		}
	}

	/**
	 * @return the stuAttenList
	 */
	public List<StudentAtten> getStuAttenList() {
		return stuAttenList;
	}

	/**
	 * @param stuAttenList
	 *            the stuAttenList to set
	 */
	public void setStuAttenList(List<StudentAtten> stuAttenList) {
		this.stuAttenList = stuAttenList;
	}

	/**
	 * @return the attenSum
	 */
	public StudentAttenSum getAttenSum() {
		return attenSum;
	}

	/**
	 * @param attenSum
	 *            the attenSum to set
	 */
	public void setAttenSum(StudentAttenSum attenSum) {
		this.attenSum = attenSum;
	}

	/**
	 * @return the classId
	 */
	public long getClassId() {
		return classId;
	}

	/**
	 * @param classId
	 *            the classId to set
	 */
	public void setClassId(long classId) {
		this.classId = classId;
	}

	/**
	 * @return the uploadImgUrl
	 */
	public String getUploadImgUrl() {
		return uploadImgUrl;
	}

	/**
	 * @param uploadImgUrl
	 *            the uploadImgUrl to set
	 */
	public void setUploadImgUrl(String uploadImgUrl) {
		this.uploadImgUrl = uploadImgUrl;
	}

	/**
	 * @return the mDate
	 */
	public String getmDate() {
		return mDate;
	}

	/**
	 * @param mDate
	 *            the mDate to set
	 */
	public void setmDate(String mDate) {
		this.mDate = mDate;
	}

	private class ViewHolder {
		private LinearLayout lyAttenSum, lyAtten;

		private TextView tvDate, tvTotal, tvLeave, tvNotAtten;

		private Button btnConfirm, btnHistory;

		private RelativeLayout rlyStudent1, rlyStudent2, rlyStudent3;

		private TextView tvStudent1, tvStudent2, tvStudent3;

		private ImageView imgv1, imgv2, imgv3;

		private DountChart01View dountChart;

	}

	private class StudentOnClickListener implements OnClickListener {
		private int index;

		private TextView tvName;

		LinearLayout lyDlg;

		private Button btnAskLeave, btnLeave, btnPhone, btnCancel;

		private boolean isCancelAskLeave = false;

		public StudentOnClickListener(int clickIndex, TextView tv) {
			index = clickIndex;
			tvName = tv;
		}

		@Override
		public void onClick(View v) {
			// tvName.setText(tvName.getText().toString() + index);

			dialog = new CustomDialog(activity, true);
			dialog.setCustomView(R.layout.avatar_choose);

			Window window = dialog.getDialog().getWindow();
			window.setGravity(Gravity.BOTTOM);
			window.setLayout(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			// WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
			//
			// //
			// 模糊度getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
			// WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
			// dialog.getWindow().setAttributes(lp);
			//
			// lp.alpha=0.5f;（0.0-1.0）//透明度，黑暗度为lp.dimAmount=1.0f;

			lyDlg = (LinearLayout) dialog.findViewById(R.id.dialog_layout);
			lyDlg.setPadding(0, 0, 0, 0);

			btnAskLeave = (Button) dialog.findViewById(R.id.btnAskLeave);
			btnLeave = (Button) dialog.findViewById(R.id.btnLeave);
			btnPhone = (Button) dialog.findViewById(R.id.btnPhone);
			btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

			int state = stuAttenList.get(index).getState();

			if (StudentAtten.ATTEN_ASK_FOR_LEAVE == state) {
				btnAskLeave.setText(R.string.cancel_ask_for_leave);
			}

			if (StudentAtten.ATTEN_LEAVE == state) {
				btnLeave.setText(R.string.cancel_no_atten);
				// isCancelLeave = true;
			}

			btnAskLeave.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// if (isCancelAskLeave)
					// {
					// stuAttenList.get(index)
					// .setState(StudentAtten.ATTEN_NORMAL);
					//
					// attenSum.setAskLeave(attenSum.getAskLeave()-1);
					// }
					// else
					// {
					// stuAttenList.get(index)
					// .setState(StudentAtten.ATTEN_ASK_FOR_LEAVE);
					//
					// attenSum.setAskLeave(attenSum.getAskLeave()+1);
					// }
					int state = stuAttenList.get(index).getState();

					switch (state) {
					case StudentAtten.ATTEN_NORMAL:
						stuAttenList.get(index).setState(
								StudentAtten.ATTEN_ASK_FOR_LEAVE);
						attenSum.setAskLeave(attenSum.getAskLeave() + 1);
						break;

					case StudentAtten.ATTEN_ASK_FOR_LEAVE:
						stuAttenList.get(index).setState(
								StudentAtten.ATTEN_NORMAL);
						attenSum.setAskLeave(attenSum.getAskLeave() - 1);
						break;

					case StudentAtten.ATTEN_LEAVE:
						stuAttenList.get(index).setState(
								StudentAtten.ATTEN_ASK_FOR_LEAVE);
						attenSum.setAskLeave(attenSum.getAskLeave() + 1);
						attenSum.setLeave(attenSum.getLeave() - 1);
						break;

					default:
						LogUtils.e(TAG + "invalid state, state=" + state);

					}

					dialog.dismiss();
					notifyDataSetChanged();
				}
			});
			btnLeave.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// if (isCancelLeave)
					// {
					// stuAttenList.get(index).setState(StudentAtten.ATTEN_NORMAL);
					//
					// attenSum.setLeave(attenSum.getLeave()-1);
					// }
					// else
					// {
					// stuAttenList.get(index).setState(StudentAtten.ATTEN_LEAVE);
					//
					// attenSum.setLeave(attenSum.getLeave()+1);
					// }

					int state = stuAttenList.get(index).getState();

					switch (state) {
					case StudentAtten.ATTEN_NORMAL:
						stuAttenList.get(index).setState(
								StudentAtten.ATTEN_LEAVE);
						attenSum.setLeave(attenSum.getLeave() + 1);
						break;

					case StudentAtten.ATTEN_ASK_FOR_LEAVE:
						stuAttenList.get(index).setState(
								StudentAtten.ATTEN_LEAVE);
						attenSum.setAskLeave(attenSum.getAskLeave() - 1);
						attenSum.setLeave(attenSum.getLeave() + 1);
						break;

					case StudentAtten.ATTEN_LEAVE:
						stuAttenList.get(index).setState(
								StudentAtten.ATTEN_NORMAL);
						attenSum.setLeave(attenSum.getLeave() - 1);
						break;

					default:
						LogUtils.e(TAG + "invalid state, state=" + state);

					}

					dialog.dismiss();
					notifyDataSetChanged();
				}
			});
			btnPhone.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_CALL);
					intent.setData(Uri.parse("tel:"
							+ stuAttenList.get(index).getParentPhone()));

					dialog.dismiss();

					activity.startActivity(intent);
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.setCancelable(true);
			dialog.show();
		}

	}

	public void uploadClassAtten(long classid) {
		ProgressDialogUtils.showProgressDialog("确认考勤中", activity, false);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("classid", String.valueOf(classid));
		params.put("filename", uploadImgUrl);
		params.put("displayKqCount", stuAttenList.size() + "");
		params.put("displayKqQj", attenSum.getAskLeave() + "");
		params.put("displayKqQq", attenSum.getLeave() + "");
		params.put("date", mDate);
		// params.put("date", "20150406");

		// 格式[{"id":1001,"state":1},{"id":1002,"state":2}]考勤状态1正常 2请假，3缺勤
		JSONArray array = new JSONArray();
		try {
			for (StudentAtten a : stuAttenList) {
				JSONObject json = new JSONObject();
				json.put("id", a.getStuId());
				json.put("state", a.getState());
				array.put(json);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			LogUtils.e(TAG + "pack jason array error!!");
		}

		params.put("data", array.toString());

		// Log.d(TAG, "data:" + array.toString());
		params.put("commandtype", "comfirmAttendance");

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_URL, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();

						if (response.optInt("ret") == 0) {
							LogUtils.d("ad 1");
							Toast.makeText(activity,
									R.string.upload_atten_sucess,
									Toast.LENGTH_SHORT).show();

							Intent intentAtten = new Intent(activity,
									KaoqinActivity.class);
							activity.startActivity(intentAtten);
							activity.finish();
						} else {
							StatusUtils.handleStatus(response, activity);
							LogUtils.e(TAG + "uploadClassAtten failed, msg="
									+ response.optString("msg"));
							LogUtils.d("ad 2");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						LogUtils.d("ad 3");
						Toast.makeText(activity, "网络异常", Toast.LENGTH_SHORT).show();
					}
				});

		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public AttenPicState getmPicState() {
		return mPicState;
	}

	public void setmPicState(AttenPicState mPicState) {
		this.mPicState = mPicState;
	}
}
