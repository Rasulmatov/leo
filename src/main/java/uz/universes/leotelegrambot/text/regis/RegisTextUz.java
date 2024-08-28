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
}
