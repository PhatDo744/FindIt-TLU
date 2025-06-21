package com.example.findittlu.ui.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.findittlu.R;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Toast;
import com.example.findittlu.utils.ImageUtils;
import com.example.findittlu.utils.CustomToast;
import android.webkit.WebView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.findittlu.adapter.ImagePagerAdapter;

public class DetailFragment extends Fragment {
    private DetailViewModel detailViewModel;

    public DetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        Bundle args = getArguments();
        long postId = args != null && args.containsKey("postId") ? args.getLong("postId", 1) : 1;
        String imageUrl = args != null ? args.getString("imageUrl", null) : null;
        ViewPager2 imagePager = view.findViewById(R.id.detail_image_pager);
        detailViewModel.getIsApiConnected().observe(getViewLifecycleOwner(), connected -> {
            if (!connected) CustomToast.showCustomToast(getContext(), "Lỗi kết nối", "Không thể kết nối API!");
        });
        detailViewModel.getDetailData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;

            android.util.Log.d("DetailFragment", "Received detail data: " + data.title + ", images: " + (data.imageUrls != null ? data.imageUrls.size() : 0));

            ((TextView) view.findViewById(R.id.detail_title)).setText(data.title);
            ((TextView) view.findViewById(R.id.detail_desc)).setText(data.description);
            ((TextView) view.findViewById(R.id.detail_date)).setText(data.date);
            ((TextView) view.findViewById(R.id.detail_time_ago)).setText(data.timeAgo);
            ((TextView) view.findViewById(R.id.detail_location)).setText(data.location);
            TextView statusView = view.findViewById(R.id.detail_status);
            if (data.isLost) {
                statusView.setText("Đồ vật bị mất");
                statusView.setBackgroundResource(R.color.logout_red);
            } else {
                statusView.setText("Đồ vật đã tìm thấy");
                statusView.setBackgroundResource(R.color.found_green);
            }
            ((TextView) view.findViewById(R.id.detail_user_name)).setText(data.userName);
            ((TextView) view.findViewById(R.id.detail_user_email)).setText(data.userEmail);
            ((TextView) view.findViewById(R.id.detail_user_phone)).setText(data.userPhone);
            ((TextView) view.findViewById(R.id.detail_category)).setText(data.categoryName);
            // Hiển thị avatar user
            ImageView avatarView = view.findViewById(R.id.detail_user_avatar);
            if (data.userAvatarUrl != null && !data.userAvatarUrl.isEmpty()) {
                android.util.Log.d("DetailFragment", "Loading user avatar: " + data.userAvatarUrl);
                ImageUtils.loadAvatar(requireContext(), data.userAvatarUrl, avatarView);
            } else {
                avatarView.setImageResource(R.drawable.ic_person);
            }

            // Hiển thị ảnh sản phẩm
            if (data.imageUrls != null && !data.imageUrls.isEmpty()) {
                android.util.Log.d("DetailFragment", "Setting up image pager with " + data.imageUrls.size() + " images");
                imagePager.setAdapter(new ImagePagerAdapter(requireContext(), data.imageUrls));
                imagePager.setVisibility(View.VISIBLE);
            } else {
                android.util.Log.d("DetailFragment", "No images to display");
                imagePager.setVisibility(View.GONE);
            }
        });
        detailViewModel.fetchDetail(postId);
        Button btnContact = view.findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(v -> {
            detailViewModel.getDetailData().observe(getViewLifecycleOwner(), data -> {
                if (data == null) return;
                String phone = data.userPhone;
                String email = data.userEmail;
                android.content.pm.PackageManager pm = requireContext().getPackageManager();
                // Liệt kê app gọi điện
                if (phone != null && !phone.isEmpty()) {
                    android.content.Intent callIntent = new android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:" + phone));
                    java.util.List<android.content.pm.ResolveInfo> callApps = pm.queryIntentActivities(callIntent, 0);
                    for (android.content.pm.ResolveInfo info : callApps) {
                        String appName = info.loadLabel(pm).toString();
                        String packageName = info.activityInfo.packageName;
                        android.util.Log.d("ContactApps", "Gọi điện: " + appName + " (" + packageName + ")");
                    }
                }
                // Liệt kê app gửi email
                if (email != null && !email.isEmpty()) {
                    android.content.Intent emailIntent = new android.content.Intent(android.content.Intent.ACTION_SENDTO, android.net.Uri.parse("mailto:" + email));
                    java.util.List<android.content.pm.ResolveInfo> emailApps = pm.queryIntentActivities(emailIntent, 0);
                    for (android.content.pm.ResolveInfo info : emailApps) {
                        String appName = info.loadLabel(pm).toString();
                        String packageName = info.activityInfo.packageName;
                        android.util.Log.d("ContactApps", "Gửi email: " + appName + " (" + packageName + ")");
                    }
                }
                // Giữ logic mở app như trước
                if (phone != null && !phone.isEmpty() && email != null && !email.isEmpty()) {
                    android.content.Intent callIntent = new android.content.Intent(android.content.Intent.ACTION_DIAL);
                    callIntent.setData(android.net.Uri.parse("tel:" + phone));
                    android.content.Intent emailIntent = new android.content.Intent(android.content.Intent.ACTION_SENDTO);
                    emailIntent.setData(android.net.Uri.parse("mailto:" + email));
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Liên hệ về tin đăng trên FindIt@TLU");
                    android.content.Intent chooser = android.content.Intent.createChooser(callIntent, "Chọn ứng dụng liên hệ");
                    chooser.putExtra(android.content.Intent.EXTRA_INITIAL_INTENTS, new android.content.Intent[]{emailIntent});
                    startActivity(chooser);
                } else if (phone != null && !phone.isEmpty()) {
                    android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_DIAL);
                    intent.setData(android.net.Uri.parse("tel:" + phone));
                    startActivity(intent);
                } else if (email != null && !email.isEmpty()) {
                    android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_SENDTO);
                    intent.setData(android.net.Uri.parse("mailto:" + email));
                    intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Liên hệ về tin đăng trên FindIt@TLU");
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Không có thông tin liên hệ", Toast.LENGTH_SHORT).show();
                }
            });
        });
        WebView mapWebView = view.findViewById(R.id.detail_map_webview);
        mapWebView.getSettings().setJavaScriptEnabled(true);
        String mapHtml = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3724.6374893374127!2d105.82223027596942!3d21.0071636885187!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135ac8109765ba5%3A0xd84740ece05680ee!2zVHLGsOG7nW5nIMSQ4bqhaSBo4buNYyBUaOG7p3kgbOG7o2k!5e0!3m2!1svi!2s!4v1750478363302!5m2!1svi!2s\" width=\"100%\" height=\"200\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade\"></iframe>";
        mapWebView.loadData(mapHtml, "text/html", "UTF-8");
    }
} 