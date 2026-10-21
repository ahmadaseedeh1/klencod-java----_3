package com.ahmadaseedeh.maybe1;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView inputDisplay;
    private TextView resultDisplay;
    private String currentExpression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // الحاوية الرئيسية للتطبيق (تصميم داكن عصري)
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor("#121212"));
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        // شاشة عرض العمليات (الإدخال)
        inputDisplay = new TextView(this);
        inputDisplay.setTextSize(22);
        inputDisplay.setTextColor(Color.parseColor("#90A4AE"));
        inputDisplay.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        inputDisplay.setPadding(32, 32, 32, 8);

        // شاشة عرض النتيجة
        resultDisplay = new TextView(this);
        resultDisplay.setTextSize(40);
        resultDisplay.setTextColor(Color.WHITE);
        resultDisplay.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        resultDisplay.setPadding(32, 8, 32, 24);

        mainLayout.addView(inputDisplay);
        mainLayout.addView(resultDisplay);

        // شبكة الأزرار العلمية (GridLayout)
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(4);
        gridLayout.setRowCount(8);
        gridLayout.setPadding(12, 12, 12, 12);

        LinearLayout.LayoutParams gridParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        );
        gridLayout.setLayoutParams(gridParams);

        // أزرار الآلة الحاسبة العلمية والعصرية
        String[] buttons = {
                "sin", "cos", "tan", "log",
                "ln", "√", "x²", "^",
                "π", "e", "!", "%",
                "AC", "DEL", "(", ")",
                "7", "8", "9", "÷",
                "4", "5", "6", "×",
                "1", "2", "3", "-",
                "0", ".", "=", "+"
        };

        for (String label : buttons) {
            Button button = new Button(this);
            button.setText(label);
            button.setTextSize(18);
            button.setIncludeFontPadding(false);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            // تم تصحيح الـ spec ليتوافق مع جميع إصدارات أندرويد بدون أي أخطاء بناء
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
            params.setMargins(6, 6, 6, 6);
            button.setLayoutParams(params);

            // تدرجات الألوان العصرية المطابقة للصورة
            if (label.equals("=")) {
                button.setBackgroundColor(Color.parseColor("#FF9100")); // برتقالي مميز للناتج
                button.setTextColor(Color.WHITE);
            } else if (label.equals("AC") || label.equals("DEL")) {
                button.setBackgroundColor(Color.parseColor("#D50000")); // أحمر للمسح
                button.setTextColor(Color.WHITE);
            } else if (label.equals("÷") || label.equals("×") || label.equals("-") || label.equals("+")) {
                button.setBackgroundColor(Color.parseColor("#37474F")); // رمادي غامق للعمليات
                button.setTextColor(Color.WHITE);
            } else if (label.matches("sin|cos|tan|log|ln|√|x²|\\^|π|e|!|%|\\(|\\)")) {
                button.setBackgroundColor(Color.parseColor("#263238")); // لون داكن للدوال العلمية
                button.setTextColor(Color.parseColor("#80D8FF"));
            } else {
                button.setBackgroundColor(Color.parseColor("#1E1E1E")); // أزرار الأرقام
                button.setTextColor(Color.WHITE);
            }

            button.setOnClickListener(this);
            gridLayout.addView(button);
        }

        mainLayout.addView(gridLayout);
        setContentView(mainLayout);
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        String buttonText = b.getText().toString();

        switch (buttonText) {
            case "AC":
                currentExpression = "";
                resultDisplay.setText("");
                break;
            case "DEL":
                if (currentExpression.length() > 0) {
                    currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
                }
                break;
            case "=":
                try {
                    double result = evaluate(currentExpression);
                    if (Double.isNaN(result) || Double.isInfinite(result)) {
                        resultDisplay.setText("خطأ رياضي");
                    } else if (result == (long) result) {
                        resultDisplay.setText(String.format("%d", (long) result));
                    } else {
                        resultDisplay.setText(String.format("%.6f", result).replaceAll("0+$", "").replaceAll("\\.$", ""));
                    }
                } catch (Exception e) {
                    resultDisplay.setText("خطأ");
                }
                break;
            case "x²":
                currentExpression += "^2";
                break;
            case "√":
                currentExpression += "sqrt(";
                break;
            case "sin":
            case "cos":
            case "tan":
            case "log":
            case "ln":
                currentExpression += buttonText + "(";
                break;
            case "π":
                currentExpression += "π";
                break;
            case "e":
                currentExpression += "e";
                break;
            case "%":
                currentExpression += "/100";
                break;
            default:
                currentExpression += buttonText;
                break;
        }

        inputDisplay.setText(currentExpression);
    }

    // محرك الحساب العلمي الداخلي
    private double evaluate(String expression) {
        expression = expression.replaceAll("π", "3.141592653589793");
        expression = expression.replaceAll("e", "2.718281828459045");
        expression = expression.replaceAll("×", "*");
        expression = expression.replaceAll("÷", "/");

        final String expr = expression;
        return new Object() {
            private int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expr.length()) throw new RuntimeException("رمز غير متوقع: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) {
                        double denom = parseFactor();
                        if (denom == 0) throw new ArithmeticException("القسمة على صفر");
                        x /= denom;
                    } else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = expr.substring(startPos, this.pos);
                    eat('(');
                    double arg = parseExpression();
                    eat(')');
                    switch (func) {
                        case "sin": x = Math.sin(Math.toRadians(arg)); break;
                        case "cos": x = Math.cos(Math.toRadians(arg)); break;
                        case "tan": x = Math.tan(Math.toRadians(arg)); break;
                        case "log": x = Math.log10(arg); break;
                        case "ln": x = Math.log(arg); break;
                        case "sqrt": x = Math.sqrt(arg); break;
                        default: throw new RuntimeException("دالة غير معروفة: " + func);
                    }
                } else {
                    throw new RuntimeException("تعبير غير صالح");
                }

                if (eat('^')) {
                    x = Math.pow(x, parseFactor());
                }
                if (eat('!')) {
                    x = factorial((int) x);
                }

                return x;
            }

            double factorial(int n) {
                if (n < 0) return Double.NaN;
                double res = 1;
                for (int i = 1; i <= n; i++) res *= i;
                return res;
            }
        }.parse();
    }
}