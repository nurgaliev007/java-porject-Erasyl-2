public class Main {
    public static void main(String[] args) {

        // 1. Клиент құру
        PersonalCustomer customer =
                new PersonalCustomer(1, "Ali", 780, "123456");

        CorporateCustomer company =
                new CorporateCustomer(2, "Company", 820,
                        "Arman", 1000000);

        System.out.println("=== KASPI MARKETPLACE ===");
        System.out.println("Клиент: " + customer.name);
        System.out.println("Клиент ID: " + customer.id);
        System.out.println("Кредит картасы: " +
                customer.creditCardNumber);

        // 2. Тауарлар құру
        Product product1 =
                new Product(1, "Laptop", 300000);

        Product product2 =
                new Product(2, "Phone", 200000);

        Product product3 =
                new Product(3, "Headphones", 50000);

        System.out.println("\n=== ТАУАРЛАР ===");
        System.out.println(product1.name + " - " + product1.price);
        System.out.println(product2.name + " - " + product2.price);
        System.out.println(product3.name + " - " + product3.price);

        // 3. Сатушы
        Seller seller = new Seller();
        seller.id = 1;
        seller.name = "Kaspi Seller";

        seller.addProduct(product1);
        seller.addProduct(product2);
        seller.addProduct(product3);

        // 4. Себетке тауар қосу
        Cart cart = new Cart();

        cart.addProduct(product1);
        cart.addProduct(product2);
        cart.addProduct(product3);

        System.out.println("\n=== СЕБЕТ ===");
        System.out.println("Тауар саны: " +
                cart.products.size());

        double total = cart.calculateTotal();

        System.out.println("Жалпы сома: " + total + " тг");

        // 5. Тапсырыс рәсімдеу
        Order order = new Order();

        order.id = 101;
        order.calculateTotal(total);
        order.updateStatus(OrderStatus.NEW);

        customer.checkout();

        System.out.println("\n=== ТАПСЫРЫС ===");
        System.out.println("Тапсырыс нөмірі: " + order.id);
        System.out.println("Тапсырыс сомасы: " +
                order.totalAmount);
        System.out.println("Статус: " + order.status);

        // 6. Тапсырыс құрамын анықтау
        OrderItem item1 = new OrderItem();
        item1.product = product1;
        item1.quantity = 1;

        OrderItem item2 = new OrderItem();
        item2.product = product2;
        item2.quantity = 1;

        OrderItem item3 = new OrderItem();
        item3.product = product3;
        item3.quantity = 1;

        System.out.println("\n=== ТАПСЫРЫС ҚҰРАМЫ ===");
        System.out.println(item1.product.name + ": " +
                item1.calculateSubtotal());
        System.out.println(item2.product.name + ": " +
                item2.calculateSubtotal());
        System.out.println(item3.product.name + ": " +
                item3.calculateSubtotal());

        // 7. Скоринг тексеру
        CreditScoringService scoring =
                new CreditScoringService();

        CreditRating rating = scoring.checkScore(customer);

        System.out.println("\n=== СКОРИНГ ТЕКСЕРУ ===");
        System.out.println("Клиент баллы: " +
                customer.getCreditScore());
        System.out.println("Кредит рейтингі: " + rating);

        // 8. Кредит таңдау
        CreditPayment credit = new CreditPayment();

        credit.amount = total;
        credit.termMonths = 12;
        credit.interestRate = 0.15;

        credit.agreement = new CreditAgreement();
        credit.agreement.agreementId = 501;
        credit.agreement.creditAmount = total;

        System.out.println("\n=== КРЕДИТ ===");

        if (scoring.approvePayment(customer,
                PaymentType.CREDIT)) {

            System.out.println("Кредит мақұлданды");
            System.out.println("Кредит сомасы: " +
                    credit.amount);
            System.out.println("Мерзімі: " +
                    credit.termMonths + " ай");
            System.out.println("Пайызы: " +
                    credit.interestRate);
            System.out.println("Келісім нөмірі: " +
                    credit.agreement.agreementId);

        } else {
            System.out.println("Кредит мақұлданбады");
        }

        // 9. Рассрочка таңдау
        InstallmentPayment installment =
                new InstallmentPayment();

        installment.amount = total;
        installment.termMonths = 6;
        installment.monthlyAmount =
                total / installment.termMonths;

        installment.plan = new InstallmentPlan();
        installment.plan.planId = 601;
        installment.plan.termMonths = 6;

        System.out.println("\n=== РАССРОЧКА ===");

        if (scoring.approvePayment(customer,
                PaymentType.INSTALLMENT)) {

            System.out.println("Рассрочка мақұлданды");
            System.out.println("Жалпы сома: " +
                    installment.amount);
            System.out.println("Мерзімі: " +
                    installment.termMonths + " ай");
            System.out.println("Ай сайынғы төлем: " +
                    installment.monthlyAmount);
            System.out.println("Жоспар нөмірі: " +
                    installment.plan.planId);

        } else {
            System.out.println("Рассрочка мақұлданбады");
        }

        // 10. Төлем өңдеу
        Payment payment = new Payment();
        payment.amount = total;

        System.out.println("\n=== ТӨЛЕМ ===");
        payment.processPayment();

        // 11. Тапсырысты растау
        order.updateStatus(OrderStatus.CONFIRMED);

        System.out.println("\n=== ҚОРЫТЫНДЫ ===");
        System.out.println("Тапсырыс расталды");
        System.out.println("Тапсырыс нөмірі: " + order.id);
        System.out.println("Соңғы статус: " + order.status);
        System.out.println("Жалпы төлем: " +
                order.totalAmount + " тг");

        System.out.println("\n=== ЖҰМЫС АЯҚТАЛДЫ ===");
    }
}
