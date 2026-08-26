import os
from PIL import Image, ImageDraw, ImageFont

# Load font helpers
def get_font(size, bold=False):
    font_name = 'segoeuib.ttf' if bold else 'segoeui.ttf'
    font_path = os.path.join('C:\\Windows\\Fonts', font_name)
    if not os.path.exists(font_path):
        font_name = 'arialbd.ttf' if bold else 'arial.ttf'
        font_path = os.path.join('C:\\Windows\\Fonts', font_name)
    return ImageFont.truetype(font_path, size)

# -------------------------------------------------------------
# FIX SCREENSHOT 1 (Vocabulary)
# -------------------------------------------------------------
def fix_screenshot_1():
    img = Image.open('store_assets/screenshot_1_vocabulary.png').convert('RGBA')
    draw = ImageDraw.Draw(img)

    # 1. Top Title (around y=120 to 160)
    # Background color: dark navy (14, 26, 50)
    # Erase old garbled title "COREE Vocabulary ard"
    draw.rectangle([320, 115, 700, 160], fill=(14, 26, 50, 255))
    font_title = get_font(28, bold=True)
    draw.text((512, 137), "CORE VOCABULARY CARD", fill=(255, 255, 255), font=font_title, anchor="mm")

    # 2. Turkish Translations: "1. Dayanikli" -> "1. Dayanıklı", "2. Esnek, Kendini Cabuk Toparlayan" -> "2. Esnek, Kendini Çabuk Toparlayan"
    # Card area around y=430 to 510
    # Erase Turkish section in the card
    draw.rectangle([345, 430, 680, 510], fill=(55, 63, 81, 255))
    font_tr = get_font(20, bold=True)
    draw.text((350, 435), "1. Dayanıklı", fill=(255, 255, 255), font=font_tr)
    draw.text((350, 470), "2. Esnek, Kendini Çabuk", fill=(255, 255, 255), font=font_tr)
    draw.text((350, 495), "Toparlayan", fill=(255, 255, 255), font=font_tr)

    # 3. English definition "Spring" -> "spring" & Example sentence quote
    # Definition line at y=535-555
    draw.rectangle([345, 535, 680, 560], fill=(15, 25, 50, 255))
    font_en_def = get_font(18, bold=False)
    draw.text((350, 538), "(adj.) Able to recoil or spring back into shape;", fill=(200, 210, 230), font=font_en_def)

    # Example sentence at y=625-660
    draw.rectangle([345, 625, 680, 660], fill=(15, 25, 50, 255))
    font_example = get_font(17, bold=False)
    draw.text((350, 628), "\"Children are often remarkably", fill=(180, 195, 220), font=font_example)
    draw.text((350, 646), "resilient to trauma.\"", fill=(180, 195, 220), font=font_example)

    # 4. Bottom Nav "Re\ view" -> "Review"
    # y=905-920, x=530-580
    draw.rectangle([530, 905, 580, 922], fill=(22, 31, 55, 255))
    font_nav = get_font(12, bold=False)
    draw.text((555, 913), "Review", fill=(160, 175, 200), font=font_nav, anchor="mm")

    img.save('store_assets/screenshot_1_vocabulary.png')
    print("Screenshot 1 fixed!")

# -------------------------------------------------------------
# FIX SCREENSHOT 2 (Exam)
# -------------------------------------------------------------
def fix_screenshot_2():
    img = Image.open('store_assets/screenshot_2_exam.png').convert('RGBA')
    draw = ImageDraw.Draw(img)

    # 1. Header title "Yos MockExami:English" -> "YDS Mock Exam: English"
    # y=120-160
    draw.rectangle([340, 120, 620, 160], fill=(73, 75, 74, 255))
    font_header = get_font(20, bold=True)
    draw.text((345, 137), "YDS Mock Exam: English", fill=(255, 255, 255), font=font_header, anchor="lm")

    # 2. Option A: "A_ contributed signi ntly" -> "A. contributed significantly"
    # y=475-515
    draw.rectangle([335, 475, 690, 515], fill=(3, 14, 34, 255))
    font_opt = get_font(18, bold=False)
    font_opt_bold = get_font(18, bold=True)
    draw.text((345, 485), "A.", fill=(64, 169, 255), font=font_opt_bold)
    draw.text((375, 485), "contributed significantly", fill=(255, 255, 255), font=font_opt)

    # 3. Option E: "E  islikely contribute" -> "E. is likely to contribute"
    # y=715-755
    draw.rectangle([335, 715, 690, 755], fill=(3, 14, 34, 255))
    draw.text((345, 725), "E.", fill=(160, 175, 200), font=font_opt_bold)
    draw.text((375, 725), "is likely to contribute", fill=(220, 230, 245), font=font_opt)

    # 4. Footer year "© 2023 Global YDS" -> "© 2026 VocabMaster"
    # y=900-920
    draw.rectangle([450, 902, 570, 920], fill=(3, 18, 41, 255))
    font_footer = get_font(13, bold=False)
    draw.text((510, 911), "© 2026 VocabMaster", fill=(140, 155, 180), font=font_footer, anchor="mm")

    img.save('store_assets/screenshot_2_exam.png')
    print("Screenshot 2 fixed!")

# -------------------------------------------------------------
# FIX SCREENSHOT 3 (Leaderboard)
# -------------------------------------------------------------
def fix_screenshot_3():
    img = Image.open('store_assets/screenshot_3_leaderboard.png').convert('RGBA')
    draw = ImageDraw.Draw(img)

    # 1. Header subtitle "LOBAL RANKING" -> "GLOBAL RANKING"
    # y=215-235
    draw.rectangle([340, 215, 500, 238], fill=(22, 23, 27, 255))
    font_sub = get_font(13, bold=True)
    draw.text((344, 226), "GLOBAL RANKING", fill=(160, 165, 175), font=font_sub, anchor="lm")

    # 2. Ben Foster score / 312 pt: -> 312 pts
    # y=430-448, x=385-430
    draw.rectangle([385, 432, 430, 448], fill=(40, 42, 48, 255))
    font_score = get_font(12, bold=False)
    draw.text((392, 440), "312 pts", fill=(255, 215, 0), font=font_score, anchor="lm")

    # 3. Bottom Nav "Commun" -> "Community"
    # y=905-920, x=480-535
    draw.rectangle([480, 905, 540, 922], fill=(22, 23, 27, 255))
    font_nav = get_font(11, bold=False)
    draw.text((505, 913), "Community", fill=(140, 145, 155), font=font_nav, anchor="mm")

    img.save('store_assets/screenshot_3_leaderboard.png')
    print("Screenshot 3 fixed!")

if __name__ == '__main__':
    fix_screenshot_1()
    fix_screenshot_2()
    fix_screenshot_3()
