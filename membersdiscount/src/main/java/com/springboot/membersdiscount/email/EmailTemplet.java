package com.springboot.membersdiscount.email;

public class EmailTemplet {

    public static final String DISCOUNT_SUBJECT = "Congratulations! You got a 15% discount";

    public static final String DISCOUNT_BODY =
                "Hello %s,\n\n" +
                        "You have received a 15%% discount!\n" +
                        "Original Price: $%.2f\n" +
                        "Discounted Price: $%.2f\n\n" +
                        "Thank you!";

    public static final String REMINDER_SUBJECT ="Payment Reminder";
    public static final String REMINDER_BODY =
            "Hello %s,\n\n" +
                    "Your payment is due on %s.\n" +
                    "Please make your payment before the deadline.\n\nThank you!";

}


