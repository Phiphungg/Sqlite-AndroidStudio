package com.pipo.sqlite;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomContactList extends BaseAdapter {
    private Activity context;
    ArrayList<Contact> contacts;
    private PopupWindow pwindo;
    SQLiteDatabaseHandler db;

    private  static final int PERMISSION_CODE = 1001;

    public CustomContactList(Activity context, ArrayList contacts, SQLiteDatabaseHandler db){
        this.context = context;
        this.contacts = contacts;
        this.db = db;
    }

    public  static class ViewHolder{
        TextView txtName;
        TextView txtPhone;
        TextView txtEMail;
        ImageView imageView;

        Button editButton;
        Button deleteButton;
    }

    @Override
    public int getCount(){return contacts.size();}

    @Override
    public Object getItem(int position) {return position;}

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder vh;
        if(convertView == null){
            vh = new CustomContactList.ViewHolder();
                  row = inflater.inflate(R.layout.layout_contact, null, true);

            vh.txtName = (TextView) row.findViewById(R.id.layoutname);
            vh.txtPhone = (TextView) row.findViewById(R.id.layoutphone);
            vh.txtEMail = (TextView) row.findViewById(R.id.layoutemail);
            vh.editButton = (Button) row.findViewById(R.id.btn_edit);
            vh.deleteButton = (Button) row.findViewById(R.id.btn_delete);

            row.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        vh.txtName.setText(contacts.get(position).getName());
        vh.txtPhone.setText("" + contacts.get(position).getPhone());
        vh.txtEMail.setText("" + contacts.get(position).getEmail());

        final int positionPopup = position;
        vh.editButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                editPopup(positionPopup);
            }
        });
        vh.deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                db.deleteContact(contacts.get(positionPopup));
                contacts = (ArrayList) db.getAllContact();
                notifyDataSetChanged();
            }
        });
        return row;
    }

    private void editPopup(final int positionPopup){
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_popup,
                (ViewGroup) context.findViewById(R.id.popup));
        pwindo = new PopupWindow(layout, 1200, 1800, true);
        pwindo.showAtLocation(layout, Gravity.CENTER,0,0);
        final EditText nameEdit = (EditText) layout.findViewById(R.id.edit_name_popup);
        final EditText phoneEdit = (EditText) layout.findViewById(R.id.edit_phone_popup);
        final EditText emailEdit = (EditText) layout.findViewById(R.id.edit_email_popup);
        nameEdit.setText(contacts.get(positionPopup).getName());
        phoneEdit.setText(contacts.get(positionPopup).getPhone());
        emailEdit.setText(contacts.get(positionPopup).getEmail());

        Button save = (Button) layout.findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String nameStr = nameEdit.getText().toString();
                String phoneStr = phoneEdit.getText().toString();
                String emailStr = emailEdit.getText().toString();

                Contact contact = contacts.get(positionPopup);
                contact.setName(nameStr);
                contact.setPhone(phoneStr);
                contact.setEmail(emailStr);
                db.updateContact(contact);
                contacts = (ArrayList) db.getAllContact();
                notifyDataSetChanged();;

                pwindo.dismiss();
            }
        });
    }
}
