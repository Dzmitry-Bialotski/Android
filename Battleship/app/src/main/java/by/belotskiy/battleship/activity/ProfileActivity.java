package by.belotskiy.battleship.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.squareup.picasso.Picasso;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.view_model.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {
    private ProfileViewModel viewModel;
    private ImageView img_view;
    private EditText username_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.NewInstanceFactory())
                .get(ProfileViewModel.class);
        username_et = findViewById(R.id.name_edit_text);
        img_view = findViewById(R.id.user_image);
        Button choose_btn = findViewById(R.id.choose_image_button);
        Button upload_btn = findViewById(R.id.upload_image_button);
        Button save_btn = findViewById(R.id.save_button);


        choose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.uploadFile(ProfileActivity.this);
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.saveName(username_et.getText().toString().trim(), ProfileActivity.this);

            }
        });
        viewModel.addChildListener(img_view, username_et);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == viewModel.IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            viewModel.imageUri = data.getData();
            Picasso.get().load(viewModel.imageUri).into(img_view);
        }
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, viewModel.IMAGE_REQUEST);
    }
}