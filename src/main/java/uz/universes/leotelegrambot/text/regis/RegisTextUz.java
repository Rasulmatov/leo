package uz.universes.leotelegrambot.text.regis;

public interface RegisTextUz {
    String nameFull="Ism Familiya yozing:";
    String phone="Telfon Number tastiqlang: \n" +
            "Tel raqam kriting:  \n" +
            "Namuna: +998912345678";
    String errorPhone="Iltimos qo'shimcha belgilarsiz yozing ‼\uFE0F \n" +
            "Namuna: +998912345678 \n" +
            " yoki pasdagi <b> Kontakt yuborish \uD83D\uDCDE</b> button bilan tastiqlang";
    String checkPhone="raqamga tastiqlash sms yuborildi \n" +
            " yuborilgan sms kodni yozing:";
    String errorCode="Kritilgan <b>kod</b> 5\uFE0F⃣ xonalik bo'lishi kerak ??";
    String infoUser="<i>Siz to'ldirgan ma'lumotlar:</i> \n\n" +
            "<b>Ism Familiya: </b> %s \n" +
            "<b>Telefon: </b> %s \n" +
            "<b>Joylashuv: </b> %s. \n" +
            "<b>Til: </b> #%s " +
            "\n\n <i>to'griligini tekshring agarda to'g'ri bo'lsa Tastiqlash tugmasini bosing </i>";
    String menu="\uD83D\uDCCC Leo Usta Botdan foydalanish qoidalari:\n" +
            "\n" +
            "1\uFE0F⃣ Maxsus bonus:\n" +
            "Leo Usta bot tomonidan berilgan maxsus bonus \uD83C\uDF81 faqat 1 yil davomida amal qiladi. 1 yil o‘tganidan so‘ng, avtomatik tarzda ogohlantirish bilan \uD83C\uDFAF berilgan bonus hisobingizdan olib tashlanadi.\n" +
            "\n" +
            "2\uFE0F⃣ Kodni ishlatish:\n" +
            "Maxsulotlar qutisidagi \uD83D\uDCE6 maxsus yopishtirilgan talonlaridagi .......7\uFE0F⃣ raqamli kodni faqat 1 martagina ishlatishingiz mumkin. Agar uni ikkinchi marotaba ishlatmoqchi bo‘lsangiz, tizim avtomatik ravishda rad etadi ❌.\n" +
            "\n" +
            "3\uFE0F⃣ O'rnatilgan maxsulotning tasdig'i:\n" +
            ".......7\uFE0F⃣ raqamli kod yuborilgandan so‘ng, o‘rnatilgan maxsulotning o‘rni va uning to‘g‘ri o‘rnatilganligini tasdiqlash uchun kamida 3 ta rasm \uD83D\uDCF8 va 3 ta rakursdan toza, tiniq holda bo‘lishi kerak.\n" +
            "\n" +
            "4\uFE0F⃣ Bonus ballar:\n" +
            "Har bir maxsulot uchun maxsus bonus ballar ⭐ mavjud va u ballar sizning akkauntingizga avtomatik tarzda hisoblanadi \uD83D\uDD22";
    String reset="Sizning ma'lumotlaringiz topilmadi, qaytadan ro'yxatdan o'tishingiz kerak!";
}
