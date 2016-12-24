package info.nukoneko.cuc.android.kidspos.common;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

/**
 * Created by atsumi on 2016/02/27.
 */
public class AsyncAPI<T> {
    private AsyncTask<Void, Void, T> myTask;

    private AsyncAPICallback callback;
    private CommonActivity mActivity = null;
    private AsyncAPI(final Builder builder){
        this.callback = builder.callback;
        if(builder.context instanceof CommonActivity){
            this.mActivity = (CommonActivity) builder.context;
        }

//        if(mActivity != null) mActivity.visibleProgress(true);

        this.myTask = new AsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... voids) {
                return (T) callback.doFunc();
            }

            @Override
            protected void onPostExecute(T ret) {
                super.onPostExecute(ret);
//                if(mActivity != null) mActivity.visibleProgress(false);
                callback.onResult(this.isCancelled() ? null : ret);
                /*
                if (builder.progressShow && builder.dialog != null) {
                    builder.dialog.dismiss();
                    builder.dialog = null;
                    if(mActivity != null) mActivity.visibleProgress(false);
                }*/
            }
        };
    }

    public static class Builder{
        private final AsyncAPICallback callback;
        private Activity context;
        private boolean progressShow = false;
        //private ProgressDialog dialog;

        public Builder(@NonNull Activity context, AsyncAPICallback callback) {
            this.callback = callback;
            this.context = context;
            System.out.println("METHOD " + context.getLocalClassName());
        }

        public Builder setProgress(Context context, String progressString, boolean cancelable){
            this.progressShow = true;
            /*
            this.dialog = new ProgressDialog(context);
            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.setMessage(progressString);
            this.dialog.setCancelable(cancelable);
            */
            return this;
        }
        public AsyncAPI build(){
            return new AsyncAPI(this);
        }
    }

    public AsyncTask<Void, Void, T> getMyTask() {
        return this.myTask;
    }

    public void run() {
        if (this.myTask == null) return;
        this.myTask.execute();
    }
}
