package net.penguincoders.doit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class CustomItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private TaskAdapter adapter;

    public CustomItemTouchHelper(TaskAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            showDeleteConfirmationDialog(position);
        } else {
            adapter.editTask(position);
        }
    }

    private void showDeleteConfirmationDialog(int position) {
        DialogHelper.showDeleteConfirmationDialog(adapter.getContext(), () -> adapter.deleteTask(position),
                () -> adapter.notifyItemChanged(position));
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        drawIconAndBackground(c, viewHolder, dX);
    }

    private void drawIconAndBackground(Canvas canvas, RecyclerView.ViewHolder viewHolder, float swipeDirectionX) {
        Context context = adapter.getContext();
        View itemView = viewHolder.itemView;
        Drawable icon = IconHelper.getIcon(context, swipeDirectionX);
        Drawable background = BackgroundHelper.getBackground(context, swipeDirectionX);

        // Draw the background and icon on the canvas
        DrawingHelper.drawOnCanvas(canvas, itemView, icon, background, swipeDirectionX);
    }
}