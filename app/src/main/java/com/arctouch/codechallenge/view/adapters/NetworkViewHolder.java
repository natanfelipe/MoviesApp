package com.arctouch.codechallenge.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arctouch.codechallenge.R;

import com.arctouch.codechallenge.util.NetworkState;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NetworkViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_error_msg)
    TextView errorMessageTextView;

    @BindView(R.id.bt_retry)
    Button retryLoadingButton;

    @BindView(R.id.progressBar)
    ProgressBar loadingProgressBar;


    private NetworkViewHolder(View itemView, RetryCallback retryCallback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        retryLoadingButton.setOnClickListener(v -> retryCallback.retry());
    }

    public void bindTo(NetworkState networkState) {
        //error message
        /*errorMessageTextView.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
        if (networkState.getMessage() != null) {
            errorMessageTextView.setText(networkState.getMessage());
        }*/

        //loading and retry
        retryLoadingButton.setVisibility(networkState.getStatus() == NetworkState.Status.FAILED ? View.VISIBLE : View.GONE);
        loadingProgressBar.setVisibility(networkState.getStatus() == NetworkState.Status.RUNNING ? View.VISIBLE : View.GONE);
    }

    public static NetworkViewHolder create(ViewGroup parent, RetryCallback retryCallback) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_network_state, parent, false);
        return new NetworkViewHolder(view, retryCallback);
    }

}
