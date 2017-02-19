package com.city.safe.data.investigation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.city.safe.R;
import com.city.safe.activity.ProjectDataActivity;
import com.city.safe.bean.FileDataEntity;
import com.city.safe.bean.UserJson;
import com.city.safe.dao.FileDataDB;
import com.city.safe.utils.FileUtils;
import com.city.safe.utils.SPUtil;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;


public class InvestigationActivity extends AppCompatActivity {
    public static final String INVESTIGATION_TYPE ="ivestigation_type";
    private LinearLayout test_layout;
    private Page the_page;
    //答案列表
    private ArrayList<Answer> the_answer_list;
    //问题列表
    private ArrayList<Quesition> the_quesition_list;
    //问题所在的View
    private View que_view;
    //答案所在的View
    private View ans_view;
    private LayoutInflater xInflater;
    private Page page;
    //下面这两个list是为了实现点击的时候改变图片，因为单选多选时情况不一样，为了方便控制
    //存每个问题下的imageview
    private ArrayList<ArrayList<ImageView>> imglist=new ArrayList<ArrayList<ImageView>>();
    //存每个答案的imageview
    private ArrayList<ImageView> imglist2;
    private String mInvestigation[];
    private ArrayList<Quesition> mQuesitions;
    private EditText mRemarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigation);
        xInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //提交按钮
        Button button=(Button)findViewById(R.id.submit);
        button.setOnClickListener(new SubmitOnClickListener(page));
        mRemarks=(EditText)findViewById(R.id.remarks);
        //假数据
        initDate();
    }
    private void initDate() {
        //初始化数据
        int type=this.getIntent().getIntExtra(INVESTIGATION_TYPE,FileDataEntity.TYPE_INV_ONE);
        switch (type){
            case FileDataEntity.TYPE_INV_ONE:
                mInvestigation=this.getResources().getStringArray(R.array.earthquake);
                setTitle("地震调查问卷");
                break;
            case FileDataEntity.TYPE_INV_TWO:
                mInvestigation=this.getResources().getStringArray(R.array.windbruch);
                setTitle("台风调查问卷");
                break;
            case FileDataEntity.TYPE_INV_THREE:
                mInvestigation=this.getResources().getStringArray(R.array.hochwasser);
                setTitle("洪灾调查问卷");
                break;
            default:
                mInvestigation=this.getResources().getStringArray(R.array.earthquake);
        }

        Log.i("hongtao.fu2",Arrays.toString(mInvestigation));
        mQuesitions=new ArrayList<Quesition>();
        for(int i=0;i<mInvestigation.length-1;i++){
            String items[]=mInvestigation[i].split("#");
            ArrayList<Answer> answers=new ArrayList<Answer>();
            for(int j=2;j<items.length;j++){
                Answer answer=new Answer();
                answer.setAnswerId("0");
                answer.setAnswer_content(items[j]);
                answer.setAns_state(0);
                answers.add(answer);
            }
            Quesition quesition=new Quesition();
            quesition.setQuesitionId(String.valueOf(i));
            quesition.setType(String.valueOf(items[0]));
            quesition.setContent(String.valueOf(items[1]));
            quesition.setAnswers(answers);
            quesition.setQue_state(0);
            mQuesitions.add(quesition);
        }
//        mRemarks.setHint(mInvestigation[mInvestigation.length-1]);
        page=new Page();
        page.setPageId("000");
        page.setStatus("0");
        page.setTitle("地震调查问卷");
        page.setQuesitions(mQuesitions);
        //加载布局
        initView(page);
    }
    private void initView(Page page) {
        // TODO Auto-generated method stub
        //这是要把问题的动态布局加入的布局
        test_layout=(LinearLayout)findViewById(R.id.lly_test);
        //获得问题即第二层的数据
        the_quesition_list=page.getQuesitions();
        //根据第二层问题的多少，来动态加载布局
        for(int i=0;i<the_quesition_list.size();i++){
            que_view=xInflater.inflate(R.layout.quesition_layout, null);
            TextView txt_que=(TextView)que_view.findViewById(R.id.txt_question_item);
            //这是第三层布局要加入的地方
            LinearLayout add_layout=(LinearLayout)que_view.findViewById(R.id.lly_answer);
            //判断单选-多选来实现后面是*号还是*多选，
            if(the_quesition_list.get(i).getType().equals("1")){
                set(txt_que,the_quesition_list.get(i).getContent(),1);
            }else{
                set(txt_que,the_quesition_list.get(i).getContent(),0);
            }
            //获得答案即第三层数据
            the_answer_list=the_quesition_list.get(i).getAnswers();
            imglist2=new ArrayList<ImageView>();
            for(int j=0;j<the_answer_list.size();j++){
                ans_view=xInflater.inflate(R.layout.answer_layout, null);
                TextView txt_ans=(TextView)ans_view.findViewById(R.id.txt_answer_item);
                ImageView image=(ImageView)ans_view.findViewById(R.id.image);
                View line_view=ans_view.findViewById(R.id.vw_line);
                if(j==the_answer_list.size()-1){
                    //最后一条答案下面不要线是指布局的问题
                    line_view.setVisibility(View.GONE);
                }
                //判断单选多选加载不同选项图片
                if(the_quesition_list.get(i).getType().equals("1")){
                    image.setBackgroundDrawable(getResources().getDrawable(R.drawable.multiselect_false));
                }else{
                    image.setBackgroundDrawable(getResources().getDrawable(R.drawable.singleselect_false));
                }
                Log.e("---", "------"+image);
                imglist2.add(image);
                txt_ans.setText(the_answer_list.get(j).getAnswer_content());
                LinearLayout lly_answer_size=(LinearLayout)ans_view.findViewById(R.id.lly_answer_size);
                lly_answer_size.setOnClickListener(new AnswerItemOnClickListener(i,j,the_answer_list,txt_ans));
                add_layout.addView(ans_view);
            }
			/*for(int r=0; r<imglist2.size();r++){
				Log.e("---", "imglist2--------"+imglist2.get(r));
			}*/

            imglist.add(imglist2);

            test_layout.addView(que_view);
        }
		/*for(int q=0;q<imglist.size();q++){
			for(int w=0;w<imglist.get(q).size();w++){
				Log.e("---", "共有------"+imglist.get(q).get(w));
			}
		}*/

    }
    private void set(TextView tv_test, String content,int type) {
        //为了加载问题后面的* 和*多选
        // TODO Auto-generated method stub
        String w;
        if(type==1){
            w = content+"";//"*[多选题]";
        }else{
            w = content+"";//"*";
        }

        int start = content.length();
        int end = w.length();
        Spannable word = new SpannableString(w);
        word.setSpan(new AbsoluteSizeSpan(25), start, end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        word.setSpan(new StyleSpan(Typeface.BOLD), start, end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        word.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv_test.setText(word);
    }
    class AnswerItemOnClickListener implements View.OnClickListener {
        private int i;
        private int j;
        private TextView txt;
        private ArrayList<Answer> the_answer_lists;
        public AnswerItemOnClickListener(int i, int j, ArrayList<Answer> the_answer_list, TextView text){
            this.i=i;
            this.j=j;
            this.the_answer_lists=the_answer_list;
            this.txt=text;

        }
        //实现点击选项后改变选中状态以及对应图片
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            //判断当前问题是单选还是多选
			/*Log.e("------", "选择了-----第"+i+"题");
			for(int q=0;q<imglist.size();q++){
				for(int w=0;w<imglist.get(q).size();w++){
//					Log.e("---", "共有------"+imglist.get(q).get(w));
				}
			}
			Log.e("----", "点击了---"+imglist.get(i).get(j));*/

            if(the_quesition_list.get(i).getType().equals("1")){
                //多选
                if(the_answer_lists.get(j).getAns_state()==0){
                    //如果未被选中
                    txt.setTextColor(Color.parseColor("#EA5514"));
                    imglist.get(i).get(j).setBackgroundDrawable(getResources().getDrawable(R.drawable.multiselect_true));
                    the_answer_lists.get(j).setAns_state(1);
                    the_quesition_list.get(i).setQue_state(1);
//                    the_answer_lists.get(j).setAnswerId(j+"");
                }else{
                    txt.setTextColor(Color.parseColor("#595757"));
                    imglist.get(i).get(j).setBackgroundDrawable(getResources().getDrawable(R.drawable.multiselect_false));
                    the_answer_lists.get(j).setAns_state(0);
                    the_quesition_list.get(i).setQue_state(1);
//                    the_answer_lists.get(j).setAnswerId(j+"");
                }
            }else{
                //单选

                for(int z=0;z<the_answer_lists.size();z++){
                    the_answer_lists.get(z).setAns_state(0);
                    imglist.get(i).get(z).setBackgroundDrawable(getResources().getDrawable(R.drawable.singleselect_false));
                }
                if(the_answer_lists.get(j).getAns_state()==0){
                    //如果当前未被选中
                    imglist.get(i).get(j).setBackgroundDrawable(getResources().getDrawable(R.drawable.singleselect_true));
                    the_answer_lists.get(j).setAns_state(1);
                    the_quesition_list.get(i).setQue_state(1);
//                    the_answer_lists.get(j).setAnswerId(j+"");
                }else{
                    //如果当前已被选中
                    the_answer_lists.get(j).setAns_state(1);
                    the_quesition_list.get(i).setQue_state(1);
//                    the_answer_lists.get(j).setAnswerId(j+"");
                }

            }
            //判断当前选项是否选中



        }

    }
    class SubmitOnClickListener implements View.OnClickListener {
        private Page page;
        public SubmitOnClickListener(Page page){
            this.page=page;
        }
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            String content="";
            //判断是否答完题
            boolean isState=true;
            //最终要的json数组
            JSONArray jsonArray = new JSONArray();
            //点击提交的时候，先判断状态，如果有未答完的就提示，如果没有再把每条答案提交（包含问卷ID 问题ID 及答案ID）
            //注：不用管是否是一个问题的答案，就以答案的个数为准来提交上述格式的数据
            for(int i=0;i<the_quesition_list.size();i++){
                the_answer_list=the_quesition_list.get(i).getAnswers();
                //判断是否有题没答完
                if(the_quesition_list.get(i).getQue_state()==0){
                    Toast.makeText(getApplicationContext(), "您第"+(i+1)+"题没有答完", Toast.LENGTH_LONG).show();
                    jsonArray=null;
                    isState=false;
                    break;
                }else{
//                    for(int j=0;j<the_answer_list.size();j++){
//                        if(the_answer_list.get(j).getAns_state()==1){
//                            JSONObject json = new JSONObject();
//                            try {
//                                json.put("pageId", page.getPageId());
//                                json.put("questionId", the_quesition_list.get(i).getQuesitionId());
//                                json.put("answerId", the_answer_list.get(j).getAnswerId());
//                                jsonArray.put(json);
//                            } catch (JSONException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        }
//                    }
                }

            }
            if(isState){
                int type=getIntent().getIntExtra(INVESTIGATION_TYPE,FileDataEntity.TYPE_INV_ONE);
                StringBuffer allAnswers=new StringBuffer(type+"|");
                for(int i=0;i<mQuesitions.size();i++){
                    Quesition quesition=mQuesitions.get(i);
                    for(int j=0;j<quesition.getAnswers().size();j++){
                        Answer answer=quesition.getAnswers().get(j);
//                        Log.i("hongtao.fu2",answer.getAnswerId()+" "+answer.getAns_state()+"  "+answer.getAnswer_content());
                        if(answer.getAns_state()==1){
                            allAnswers.append(String.valueOf(j));
                        }
                    }
                    allAnswers.append("|");
                }
                allAnswers.append(mRemarks.getText().toString());
                Log.i("hongtao.fu2",allAnswers.toString());
                tipsSubmit(allAnswers.toString());
            }

        }
    }
    public void tipsSubmit(final String content){
        //获得屏幕的像素密度
        float scale = getResources().getDisplayMetrics().density;
        //计算出一50dp相当与多少px
        int size = (int) (50 * scale + 0.5f);// dp 转 px
        //从资源中加载一张图片到内存里
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.city);
        //将图片压缩/放大为size*size的大小
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
        //创建一个Alertilaog的builder，并且在Dialog上面设置提提示的信息和事件的处理代码
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(new BitmapDrawable(bitmap));
        builder.setTitle("提醒");
        builder.setMessage("提交调查问卷");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long time=System.currentTimeMillis();
                try {
                    String userInfoJson = SPUtil.getValueByKey(getApplicationContext(), SPUtil.USER_INFO);
                    Gson gson = new Gson();
                    UserJson user = gson.fromJson(userInfoJson, UserJson.class);
                    int type = getIntent().getIntExtra(INVESTIGATION_TYPE,FileDataEntity.TYPE_INV_ONE);
                    String name=type+"_investigation_"+Long.toHexString(time).toUpperCase()+".txt";
                    String path=FileUtils.saveFileToProject(InvestigationActivity.this,content,name);
                    if(path!=null && !"".equals(path.trim())) {
                        FileDataEntity fileDataEntity = new FileDataEntity
                                (user.getId(),
                                        getIntent().getStringExtra(ProjectDataActivity.PROJECT_ID),
                                        "FILE_ID_" + Long.toHexString(time).toUpperCase(),
                                        name,
                                        path,
                                        type, 0,
                                        time
                                );
                        FileDataDB.getIntance().insert(fileDataEntity);

                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("取消", null);
        //创建出Dilaog
        AlertDialog dialog = builder.create();
        //将创建出来的Dialog显示在界面上
        dialog.show();
    }

}
