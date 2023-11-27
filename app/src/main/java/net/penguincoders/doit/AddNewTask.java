package net.penguincoders.doit;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.Objects;

public class TaskEditorBottomSheetDialog extends BottomSheetDialogFragment {

    private EditText taskEditText;
    private Button saveButton;
    private DatabaseHandler databaseHandler;
    private boolean isUpdate = false;
    private int taskIdToUpdate = -1;

    public static TaskEditorBottomSheetDialog newInstance() {
        return new TaskEditorBottomSheetDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TaskEditorDialogStyle);
        databaseHandler = new DatabaseHandler(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_task_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskEditText = view.findViewById(R.id.taskEditText);
        saveButton = view.findViewById(R.id.saveButton);

        Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            taskIdToUpdate = bundle.getInt("taskId", -1);
            String task = bundle.getString("taskDescription", "");
            taskEditText.setText(task);
            toggleSaveButtonEnabled(task);
        }

        setupTextChangeListener();
        setupSaveButtonClickListener();
    }

    private void setupTextChangeListener() {
        taskEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toggleSaveButtonEnabled(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void toggleSaveButtonEnabled(String taskDescription) {
        if (taskDescription.trim().isEmpty()) {
            saveButton.setEnabled(false);
            saveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.grayDisabledColor));
        } else {
            saveButton.setEnabled(true);
            saveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        }
    }

    private void setupSaveButtonClickListener() {
        saveButton.setOnClickListener(v -> {
            String taskDescription = taskEditText.getText().toString().trim();
            if (isUpdate) {
                if (taskIdToUpdate != -1) {
                    databaseHandler.updateTask(taskIdToUpdate, taskDescription);
                }
            } else {
                ToDoModel newTask = new ToDoModel();
                newTask.setTask(taskDescription);
                newTask.setStatus(0);
                databaseHandler.insertTask(newTask);
            }
            dismiss();
            if (getActivity() instanceof TaskEditorListener) {
                ((TaskEditorListener) getActivity()).onTaskSaved();
            }
        });
    }
}
