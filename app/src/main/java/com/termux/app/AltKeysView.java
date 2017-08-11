package com.termux.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridLayout;

import com.termux.R;
import com.termux.terminal.TerminalSession;
import com.termux.view.TerminalView;

/**
 * A view showing extra keys (such as Escape, Ctrl, Alt) not normally available on an Android soft
 * keyboard.
 */
public final class AltKeysView extends GridLayout {

    private static final int TEXT_COLOR = 0xFFFFFFFF;

    public AltKeysView(Context context, AttributeSet attrs) {
        super(context, attrs);

        reload();
    }

    static void sendKey(View view, String keyName) {
        int keyCode = 0;
        String chars = null;
        switch (keyName) {
            case "HOME":
                keyCode = KeyEvent.KEYCODE_MOVE_HOME;
                break;
            case "END":
                keyCode = KeyEvent.KEYCODE_MOVE_END;
                break;
            case "▲":
                keyCode = KeyEvent.KEYCODE_DPAD_UP;
                break;
            case "◀":
                keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
                break;
            case "▶":
                keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
                break;
            case "▼":
                keyCode = KeyEvent.KEYCODE_DPAD_DOWN;
                break;
            case "―":
                chars = "-";
                break;
            default:
                chars = keyName;
        }

        if (keyCode > 0) {
            view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
            view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
        } else {
            TerminalView terminalView = (TerminalView) view.findViewById(R.id.terminal_view);
            TerminalSession session = terminalView.getCurrentSession();
            if (session != null) session.write(chars);
        }
    }

    void reload() {
        removeAllViews();

        String[][] buttons = {
            {"HOME", "END", "◀", "▲", "▼", "▶"}
        };

        final int rows = buttons.length;
        final int cols = buttons[0].length;

        setRowCount(rows);
        setColumnCount(cols);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                final String buttonText = buttons[row][col];

                AutoRepeatButton button = new AutoRepeatButton(getContext(), null, android.R.attr.buttonBarButtonStyle);
                final AutoRepeatButton finalButton = new AutoRepeatButton(getContext(), null, android.R.attr.buttonBarButtonStyle);

                button.setText(buttonText);
                button.setTextColor(TEXT_COLOR);

                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalButton.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                        View root = getRootView();
                        sendKey(root, buttonText);
                    }
                });

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = param.width = 0;
                param.rightMargin = param.topMargin = 0;
                param.setGravity(Gravity.START);
                float weight = "▲▼◀▶".contains(buttonText) ? 0.7f : 1.f;
                param.columnSpec = GridLayout.spec(col, GridLayout.FILL, weight);
                param.rowSpec = GridLayout.spec(row, GridLayout.FILL, 1.f);
                button.setLayoutParams(param);

                addView(button);
            }
        }
    }

}
