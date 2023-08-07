package com.pipo.sqlite;

import static com.pipo.sqlite.Manifest.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Contact> contacts;
    SQLiteDatabaseHandler db;
    Button btnadd;
    PopupWindow pwindo;
    Activity activity;
    ListView listView;
    CustomContactList customContactList;

    private ImageView imageView;
    private Uri fileUri;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        db = new SQLiteDatabaseHandler(this);
        listView = (ListView) findViewById(R.id.list_view);
        btnadd = (Button) findViewById(R.id.btn_add);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPopUp();
            }
        });

        contacts = (ArrayList) db.getAllContact();


        CustomContactList customContactList = new CustomContactList(this, contacts, db);
        listView.setAdapter(customContactList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "You Selected " + contacts.get(position).getName() + " as Contact", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addPopUp() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_popup,
                (ViewGroup) activity.findViewById(R.id.popup));
        pwindo = new PopupWindow(layout, 1200, 1800, true);
        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        final EditText nameEdit = (EditText) layout.findViewById(R.id.edit_name_popup);
        final EditText phoneEdit = (EditText) layout.findViewById(R.id.edit_phone_popup);
        final EditText emailEdit = (EditText) layout.findViewById(R.id.edit_email_popup);

        imageView = (ImageView) layout.findViewById(R.id.image_popup);
        final Button choose_image = (Button) layout.findViewById(R.id.btn_choose_image);

        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickImageFromGallery();
                    }
                } else {
                    pickImageFromGallery();
                }
            }
        });

        Button save = (Button) layout.findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameStr = nameEdit.getText().toString();
                String emailStr = emailEdit.getText().toString();
                String phoneStr = phoneEdit.getText().toString();
                String filepath;

                if (fileUri != null) {
                    filepath = fileUri.toString();
                } else {
                    filepath = "";
                }
                Contact contact = new Contact(nameStr, phoneStr, emailStr, filepath);
                db.addContact(contact);

                if (customContactList == null) {
                    customContactList = new CustomContactList(activity, contacts, db);
                    listView.setAdapter(customContactList);
                }
                customContactList.contacts = (ArrayList) db.getAllContact();
                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();

                pwindo.dismiss();
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent((Intent.ACTION_PICK));
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            fileUri = data.getData();
            File photoFile = new File(fileUri + ".ipg");
            Uri photoUri = Uri.fromFile(photoFile.getAbsoluteFile());
            imageView.setImageURI(fileUri);

            Toast.makeText(this, fileUri.toString(), Toast.LENGTH_LONG);
        }
    }

    public class CustomContactList extends BaseAdapter {
        private Activity context;
        ArrayList<Contact> contacts;
        private PopupWindow pwindo;
        SQLiteDatabaseHandler db;

        private static final int PERMISSION_CODE = 1001;

        public CustomContactList(Activity context, ArrayList contacts, SQLiteDatabaseHandler db) {
            this.context = context;
            this.contacts = contacts;
            this.db = db;
        }

        public class ViewHolder {
            TextView txtName;
            TextView txtPhone;
            TextView txtEMail;
            ImageView imageView;

            Button editButton;
            Button deleteButton;
        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            LayoutInflater inflater = context.getLayoutInflater();
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                row = inflater.inflate(R.layout.layout_contact, null, true);

                vh.txtName = (TextView) row.findViewById(R.id.layoutname);
                vh.txtPhone = (TextView) row.findViewById(R.id.layoutphone);
                vh.txtEMail = (TextView) row.findViewById(R.id.layoutemail);
                vh.editButton = (Button) row.findViewById(R.id.btn_edit);
                vh.deleteButton = (Button) row.findViewById(R.id.btn_delete);
                vh.imageView = (ImageView) row.findViewById(R.id.layoutimage);

                // store the holder with the view.
                row.setTag(vh);
            } else {

                vh = (ViewHolder) convertView.getTag();

            }

            vh.txtName.setText(contacts.get(position).getName());
            vh.txtPhone.setText("" + contacts.get(position).getPhone());
            vh.txtEMail.setText("" + contacts.get(position).getEmail());

            if (contacts.get(position).getImage() != null) {
                Uri uri = Uri.parse(contacts.get(position).getImage());
                vh.imageView.setImageURI(uri);
            }

            final int positionPopup = position;
            vh.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    editPopup(positionPopup);
                }
            });

            vh.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //     Integer index = (Integer) view.getTag();
                    db.deleteContact(contacts.get(positionPopup));

                    //      countries.remove(index.intValue());
                    contacts = (ArrayList) db.getAllContact();
                    notifyDataSetChanged();
                }
            });
            return row;
        }

        public void editPopup(final int positionPopup) {
            LayoutInflater inflater = context.getLayoutInflater();
            View layout = inflater.inflate(R.layout.layout_popup,
                    (ViewGroup) context.findViewById(R.id.popup));
            pwindo = new PopupWindow(layout, 1200, 1800, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            final EditText nameEdit = (EditText) layout.findViewById(R.id.edit_name_popup);
            final EditText phoneEdit = (EditText) layout.findViewById(R.id.edit_phone_popup);
            final EditText emailEdit = (EditText) layout.findViewById(R.id.edit_email_popup);
            imageView = (ImageView) layout.findViewById(R.id.image_popup);
            nameEdit.setText(contacts.get(positionPopup).getName());
            phoneEdit.setText("" + contacts.get(positionPopup).getPhone());
            emailEdit.setText("" + contacts.get(positionPopup).getEmail());

            if (contacts.get(positionPopup).getImage() != null) {
                Uri uri = Uri.parse(contacts.get(positionPopup).getImage());
                imageView.setImageURI(uri);

                Button btn_choose_image = (Button) layout.findViewById(R.id.btn_choose_image);
                btn_choose_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                String[] permissions = {permission.READ_EXTERNAL_STORAGE};
                                requestPermissions(permissions, PERMISSION_CODE);
                            } else {
                                pickImageFromGallery();
                            }
                        } else {
                            pickImageFromGallery();
                        }
                    }
                });

                Button save = (Button) layout.findViewById(R.id.btn_save);
                save.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String nameStr = nameEdit.getText().toString();
                        String phoneStr = phoneEdit.getText().toString();
                        String emailStr = emailEdit.getText().toString();

                        Contact contact = contacts.get(positionPopup);
                        contact.setName(nameStr);
                        contact.setPhone(phoneStr);
                        contact.setEmail(emailStr);
                        contact.setImage(fileUri.toString());
                        db.updateContact(contact);
                        contacts = (ArrayList) db.getAllContact();
                        notifyDataSetChanged();

                        pwindo.dismiss();
                    }
                });
            }
        }
    }
}



