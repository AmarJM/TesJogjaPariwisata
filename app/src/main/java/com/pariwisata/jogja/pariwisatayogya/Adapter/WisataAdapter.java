package com.pariwisata.jogja.pariwisatayogya.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pariwisata.jogja.pariwisatayogya.Model.SemuaTempat;
import com.pariwisata.jogja.pariwisatayogya.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import me.biubiubiu.justifytext.library.JustifyTextView;

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.WisataHolder>{
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    Context mContext;
    List<SemuaTempat> mSemuaTempats;

    public WisataAdapter(Context mContext, List<SemuaTempat> mSemuaTempats) {
        this.mContext = mContext;
        this.mSemuaTempats = mSemuaTempats;
    }

    @Override
    public WisataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tempat, parent, false);
        return new WisataAdapter.WisataHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WisataHolder holder, int position) {
        final SemuaTempat item = mSemuaTempats.get(position);
        final int radius = 10;
        final int margin = 8;
        final Transformation transformation = new RoundedCornersTransformation(radius, margin);
        holder.namaTempat.setText(item.getNamaPariwisata());
        Picasso.with(mContext)
                .load(item.getGambarPariwisata())
                .transform(transformation)
                .into(holder.imgWisata);

        holder.imgWisata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView img;
                JustifyTextView tvDetail;
                TextView tvAlamat;
                dialog = new AlertDialog.Builder(view.getContext());
                inflater = LayoutInflater.from(view.getContext());
                View dialogView = inflater.inflate(R.layout.detail_tempat, null);
                dialog.setView(dialogView);
                dialog.setTitle(item.getNamaPariwisata());
                dialog.setCancelable(true);
                dialog.setIcon(R.drawable.wisata72);
                img    =  dialogView.findViewById(R.id.dialog_imageview);
                tvDetail = dialogView.findViewById(R.id.dialog_detail);
                tvAlamat = dialogView.findViewById(R.id.detail_alamat);
                tvDetail.setText(item.getDetailPariwisata());
                tvAlamat.setText(item.getAlamatPariwisata());
                Picasso.with(view.getContext()).load(item.getGambarPariwisata()).into(img);
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        holder.imgAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String alamatPeta = "http://maps.google.co.in/maps?q=" + item.getNamaPariwisata();
                Intent bukaPeta = new Intent(Intent.ACTION_VIEW, Uri.parse(alamatPeta));
                view.getContext().startActivity(bukaPeta);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSemuaTempats.size();
    }

    public class WisataHolder extends RecyclerView.ViewHolder {
        TextView namaTempat;
        ImageView imgWisata, imgAlamat;
        public WisataHolder(View itemView) {
            super(itemView);
            namaTempat = itemView.findViewById(R.id.vaddress);
            imgWisata = itemView.findViewById(R.id.thumbnail);
            imgAlamat = itemView.findViewById(R.id.imgLocation);
        }
    }
}
