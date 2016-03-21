package br.unisul.unifind.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import java.util.List;

import br.unisul.unifind.objetos.Local;

/**
 * Created by Ronan Cardoso on 29/10/2015.
 */
public class adapter extends BaseAdapter {
    private Context context;
    private List<Local> locais;

    public adapter(Context context, List<Local> locais) {
        this.context = context;
        this.locais = locais;
    }

    @Override
    public int getCount() {
        return locais.size();
    }

    @Override
    public Object getItem(int position) {
        return locais.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TwoLineListItem twoLineListItem;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) inflater.inflate(
                    android.R.layout.simple_list_item_2, null);
        } else {
            twoLineListItem = (TwoLineListItem) convertView;
        }

        TextView text1 = twoLineListItem.getText1();
        TextView text2 = twoLineListItem.getText2();

        text1.setText(locais.get(position).getDescricao());
        text2.setText("(" + locais.get(position).getLatitude()+", "+ locais.get(position).getLongitude()+")");

        return twoLineListItem;
    }
}
