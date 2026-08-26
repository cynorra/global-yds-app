import os
from PIL import Image, ImageDraw, ImageFont, ImageFilter

def get_font(size, bold=False):
    font_name = 'segoeuib.ttf' if bold else 'segoeui.ttf'
    font_path = os.path.join('C:\\Windows\\Fonts', font_name)
    if not os.path.exists(font_path):
        font_name = 'arialbd.ttf' if bold else 'arial.ttf'
        font_path = os.path.join('C:\\Windows\\Fonts', font_name)
    return ImageFont.truetype(font_path, size)

def create_gradient_bg(width, height, color1, color2):
    base = Image.new('RGBA', (width, height), color1)
    top = Image.new('RGBA', (width, height), color2)
    mask = Image.new('L', (width, height))
    mask_draw = ImageDraw.Draw(mask)
    for y in range(height):
        alpha = int(255 * (y / height))
        mask_draw.line([(0, y), (width, y)], fill=alpha)
    base.paste(top, (0, 0), mask)
    return base

def draw_rounded_rect(draw, xy, radius, fill=None, outline=None, width=1):
    draw.rounded_rectangle(xy, radius=radius, fill=fill, outline=outline, width=width)

# ==============================================================================
# 1. APP ICON (Exact 512 x 512 px PNG & Android Launcher Icons)
# ==============================================================================
def build_app_icon():
    src_path = 'store_assets/512x512_app_icon_v2.png'
    if not os.path.exists(src_path):
        src_path = 'store_assets/512x512_app_icon.png'
    img = Image.open(src_path).convert('RGBA')
    icon_512 = img.resize((512, 512), Image.Resampling.LANCZOS)
    out_path = 'store_assets/app_icon_512x512.png'
    icon_512.save(out_path, 'PNG', optimize=True)
    print(f"App Icon saved: {out_path} (512x512 px)")

    # Also update Android Mipmap Launcher Icons in app/src/main/res
    mipmap_sizes = {
        'mipmap-mdpi': 48,
        'mipmap-hdpi': 72,
        'mipmap-xhdpi': 96,
        'mipmap-xxhdpi': 144,
        'mipmap-xxxhdpi': 192
    }

    # Circular mask helper
    circle_mask_512 = Image.new('L', (512, 512), 0)
    c_draw = ImageDraw.Draw(circle_mask_512)
    c_draw.ellipse([0, 0, 512, 512], fill=255)

    img_round_512 = icon_512.copy()
    img_round_512.putalpha(circle_mask_512)

    res_dir = 'app/src/main/res'
    if os.path.exists(res_dir):
        for folder, size in mipmap_sizes.items():
            folder_path = os.path.join(res_dir, folder)
            os.makedirs(folder_path, exist_ok=True)

            # Square launcher icon
            resized_sq = icon_512.resize((size, size), Image.Resampling.LANCZOS)
            resized_sq.save(os.path.join(folder_path, 'ic_launcher.png'), 'PNG', optimize=True)

            # Round launcher icon
            resized_round = img_round_512.resize((size, size), Image.Resampling.LANCZOS)
            resized_round.save(os.path.join(folder_path, 'ic_launcher_round.png'), 'PNG', optimize=True)

        print("Updated Android launcher icons across all mipmap folders.")

# ==============================================================================
# 2. FEATURE GRAPHIC (Exact 1024 x 500 px PNG)
# ==============================================================================
def build_feature_graphic():
    W, H = 1024, 500
    img = create_gradient_bg(W, H, (11, 19, 43), (26, 46, 88))
    draw = ImageDraw.Draw(img)

    # Decorative geometric glowing circles/accents
    acc = Image.new('RGBA', (W, H), (0, 0, 0, 0))
    acc_draw = ImageDraw.Draw(acc)
    acc_draw.ellipse([700, -100, 1150, 350], fill=(59, 130, 246, 30))
    acc_draw.ellipse([800, 150, 1100, 450], fill=(99, 102, 241, 40))
    acc_draw.ellipse([-100, 200, 300, 600], fill=(14, 165, 233, 25))
    img.paste(acc, (0, 0), acc)
    draw = ImageDraw.Draw(img)

    # Embed Icon Preview on left (size: 260x260)
    src_icon = 'store_assets/512x512_app_icon_v2.png' if os.path.exists('store_assets/512x512_app_icon_v2.png') else 'store_assets/512x512_app_icon.png'
    icon_img = Image.open(src_icon).convert('RGBA')
    icon_res = icon_img.resize((240, 240), Image.Resampling.LANCZOS)
    
    # Mask rounded corners for icon
    mask = Image.new('L', (240, 240), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.rounded_rectangle([0, 0, 240, 240], radius=44, fill=255)
    
    # Icon position (x: 80, y: 130)
    img.paste(icon_res, (80, 130), mask)
    # Icon border highlight
    draw_rounded_rect(draw, [80, 130, 320, 370], radius=44, outline=(99, 179, 237), width=3)

    # Text Branding on Right (x: 370)
    draw.text((370, 120), "VOCABMASTER", fill=(255, 255, 255), font=get_font(46, bold=True))
    draw.text((370, 180), "Master English Vocabulary & Exams", fill=(96, 165, 250), font=get_font(24, bold=True))
    draw.text((370, 220), "Learn faster with smart flashcards, timed mock tests, and global rankings.", fill=(203, 213, 225), font=get_font(17, bold=False))

    # Feature Badges/Pills
    badges = [("⚡ Smart Flashcards", (30, 58, 138)), ("📝 Mock Exams", (30, 41, 59)), ("🏆 Global Leaderboard", (30, 41, 59))]
    bx = 370
    by = 285
    for label, bg_color in badges:
        font_b = get_font(15, bold=True)
        tw = int(font_b.getlength(label)) + 30
        draw_rounded_rect(draw, [bx, by, bx+tw, by+44], radius=22, fill=bg_color, outline=(59, 130, 246), width=1)
        draw.text((bx + tw//2, by + 22), label, fill=(241, 245, 249), font=font_b, anchor="mm")
        bx += tw + 15

    # Footer CTA Pill
    draw_rounded_rect(draw, [370, 360, 610, 415], radius=27, fill=(37, 99, 235))
    draw.text((490, 387), "START LEARNING NOW ►", fill=(255, 255, 255), font=get_font(15, bold=True), anchor="mm")

    out_path = 'store_assets/feature_graphic_1024x500.png'
    img.save(out_path, 'PNG', optimize=True)
    print(f"Feature Graphic saved: {out_path} (1024x500 px)")

# ==============================================================================
# HELPER FOR SCREENSHOT FRAMING (9:16 Aspect Ratio)
# ==============================================================================
def render_916_screenshot(title, subtitle, content_type, out_w, out_h, out_path):
    img = create_gradient_bg(out_w, out_h, (11, 19, 43), (19, 34, 68))
    draw = ImageDraw.Draw(img)

    # Scale factors relative to 1080x1920 base
    scale = out_w / 1080.0

    # Top Marketing Text
    font_title = get_font(int(46 * scale), bold=True)
    font_sub = get_font(int(24 * scale), bold=False)

    draw.text((out_w//2, int(90 * scale)), title, fill=(99, 179, 237), font=font_title, anchor="mm")
    draw.text((out_w//2, int(150 * scale)), subtitle, fill=(160, 174, 192), font=font_sub, anchor="mm")

    # Frame Dimensions
    fx1 = int(90 * scale)
    fy1 = int(210 * scale)
    fx2 = out_w - fx1
    fy2 = out_h - int(60 * scale)

    # Outer Phone / Tablet Frame
    draw_rounded_rect(draw, [fx1, fy1, fx2, fy2], radius=int(44 * scale), fill=(26, 32, 44), outline=(74, 85, 104), width=int(4 * scale))
    draw_rounded_rect(draw, [fx1+int(16*scale), fy1+int(16*scale), fx2-int(16*scale), fy2-int(16*scale)], radius=int(32 * scale), fill=(15, 23, 42))

    # Inner Screen Render
    sw = fx2 - fx1 - int(32 * scale)
    sh = fy2 - fy1 - int(32 * scale)
    sx1 = fx1 + int(16 * scale)
    sy1 = fy1 + int(16 * scale)

    # Render Content inside Device Frame
    if content_type == 'vocabulary':
        # Flashcard UI
        draw.text((sx1 + int(40*scale), sy1 + int(50*scale)), "Unit 4: Advanced Vocab", fill=(241, 245, 249), font=get_font(int(24*scale), bold=True))
        draw.text((sx1 + sw - int(40*scale), sy1 + int(50*scale)), "15 / 20 Learned", fill=(148, 163, 184), font=get_font(int(20*scale)), anchor="ra")

        # Progress bar
        pb_y = sy1 + int(95*scale)
        draw_rounded_rect(draw, [sx1+int(40*scale), pb_y, sx1+sw-int(40*scale), pb_y+int(12*scale)], radius=int(6*scale), fill=(30, 41, 59))
        draw_rounded_rect(draw, [sx1+int(40*scale), pb_y, sx1+int((sw-80*scale)*0.75), pb_y+int(12*scale)], radius=int(6*scale), fill=(59, 130, 246))

        # Main Card
        cx1, cy1, cx2, cy2 = sx1+int(40*scale), sy1+int(140*scale), sx1+sw-int(40*scale), sy1+sh-int(180*scale)
        draw_rounded_rect(draw, [cx1, cy1, cx2, cy2], radius=int(28*scale), fill=(30, 41, 59), outline=(51, 65, 85), width=int(2*scale))

        draw.text((sx1+sw//2, cy1+int(60*scale)), "Resilient", fill=(255, 255, 255), font=get_font(int(52*scale), bold=True), anchor="mm")
        draw.text((sx1+sw//2, cy1+int(125*scale)), "/rɪˈzɪl.i.ənt/  🔊", fill=(148, 163, 184), font=get_font(int(26*scale)), anchor="mm")

        draw.line([(cx1+int(40*scale), cy1+int(165*scale)), (cx2-int(40*scale), cy1+int(165*scale))], fill=(51, 65, 85), width=1)

        draw.text((cx1+int(40*scale), cy1+int(195*scale)), "TURKISH MEANINGS", fill=(96, 165, 250), font=get_font(int(20*scale), bold=True))
        draw.text((cx1+int(40*scale), cy1+int(235*scale)), "1. Dayanıklı", fill=(241, 245, 249), font=get_font(int(26*scale), bold=True))
        draw.text((cx1+int(40*scale), cy1+int(275*scale)), "2. Esnek, Kendini Çabuk Toparlayan", fill=(241, 245, 249), font=get_font(int(26*scale), bold=True))

        draw.text((cx1+int(40*scale), cy1+int(340*scale)), "DEFINITION & USAGE", fill=(96, 165, 250), font=get_font(int(20*scale), bold=True))
        draw.text((cx1+int(40*scale), cy1+int(380*scale)), "(adj.) Able to recoil or spring back into shape;\nable to withstand or recover quickly from difficult conditions.", fill=(203, 213, 225), font=get_font(int(22*scale)))

        ex_y1 = cy1 + int(490*scale)
        draw_rounded_rect(draw, [cx1+int(30*scale), ex_y1, cx2-int(30*scale), ex_y1+int(120*scale)], radius=int(16*scale), fill=(15, 23, 42), outline=(30, 58, 138), width=1)
        draw.text((cx1+int(50*scale), ex_y1+int(20*scale)), "EXAMPLE", fill=(59, 130, 246), font=get_font(int(18*scale), bold=True))
        draw.text((cx1+int(50*scale), ex_y1+int(58*scale)), "\"Children are often remarkably resilient to trauma.\"", fill=(226, 232, 240), font=get_font(int(22*scale)))

        # Controls under card
        ctrl_y = cy2 + int(30*scale)
        draw.text((cx1+int(20*scale), ctrl_y), "◄ SWIPE LEFT", fill=(239, 68, 68), font=get_font(int(20*scale), bold=True))
        draw.text((cx1+int(20*scale), ctrl_y+int(30*scale)), "Keep Learning", fill=(148, 163, 184), font=get_font(int(16*scale)))

        draw.text((cx2-int(20*scale), ctrl_y), "SWIPE RIGHT ►", fill=(34, 197, 94), font=get_font(int(20*scale), bold=True), anchor="ra")
        draw.text((cx2-int(20*scale), ctrl_y+int(30*scale)), "Mark Learned", fill=(148, 163, 184), font=get_font(int(16*scale)), anchor="ra")

    elif content_type == 'exam':
        # Mock Exam UI
        draw.text((sx1+int(40*scale), sy1+int(50*scale)), "Question 14 of 40", fill=(241, 245, 249), font=get_font(int(24*scale), bold=True))
        
        draw_rounded_rect(draw, [sx1+sw-int(220*scale), sy1+int(35*scale), sx1+sw-int(40*scale), sy1+int(80*scale)], radius=int(20*scale), fill=(30, 41, 59), outline=(225, 29, 72), width=1)
        draw.text((sx1+sw-int(130*scale), sy1+int(58*scale)), "⏱ 03:36:47", fill=(244, 63, 94), font=get_font(int(18*scale), bold=True), anchor="mm")

        # Question box
        qx1, qy1, qx2, qy2 = sx1+int(40*scale), sy1+int(110*scale), sx1+sw-int(40*scale), sy1+int(380*scale)
        draw_rounded_rect(draw, [qx1, qy1, qx2, qy2], radius=int(24*scale), fill=(30, 41, 59), outline=(51, 65, 85), width=1)
        draw.text((qx1+int(30*scale), qy1+int(30*scale)), "VOCABULARY & PROFICIENCY", fill=(59, 130, 246), font=get_font(int(18*scale), bold=True))
        
        q_text = "Despite the challenging economic climate, the company managed to maintain its growth by adopting innovative strategies that _______ to its resilience and long-term success."
        words = q_text.split()
        lines, current_line = [], ""
        font_q = get_font(int(23*scale))
        for word in words:
            test_line = current_line + (" " if current_line else "") + word
            if font_q.getlength(test_line) <= (qx2 - qx1 - int(60*scale)):
                current_line = test_line
            else:
                lines.append(current_line)
                current_line = word
        if current_line:
            lines.append(current_line)

        for idx, line in enumerate(lines):
            draw.text((qx1+int(30*scale), qy1+int(75*scale) + idx*int(36*scale)), line, fill=(241, 245, 249), font=font_q)

        # Options
        options = [
            ("A", "contributed significantly", True),
            ("B", "were attributed only", False),
            ("C", "have to be attributed", False),
            ("D", "was leading effectively", False),
            ("E", "is likely to contribute", False)
        ]
        opt_start_y = qy2 + int(30*scale)
        for idx, (letter, text, selected) in enumerate(options):
            oy1 = opt_start_y + idx * int(105*scale)
            oy2 = oy1 + int(90*scale)
            ox1, ox2 = qx1, qx2

            bg_color = (30, 58, 138) if selected else (30, 41, 59)
            border_color = (59, 130, 246) if selected else (51, 65, 85)
            draw_rounded_rect(draw, [ox1, oy1, ox2, oy2], radius=int(22*scale), fill=bg_color, outline=border_color, width=2 if selected else 1)

            badge_bg = (59, 130, 246) if selected else (51, 65, 85)
            badge_fg = (255, 255, 255) if selected else (148, 163, 184)
            draw_rounded_rect(draw, [ox1+int(20*scale), oy1+int(18*scale), ox1+int(72*scale), oy1+int(72*scale)], radius=int(14*scale), fill=badge_bg)
            draw.text((ox1+int(46*scale), oy1+int(45*scale)), letter, fill=badge_fg, font=get_font(int(22*scale), bold=True), anchor="mm")

            draw.text((ox1+int(95*scale), oy1+int(45*scale)), text, fill=(255, 255, 255) if selected else (226, 232, 240), font=get_font(int(22*scale), bold=selected), anchor="lm")
            if selected:
                draw.text((ox2-int(40*scale), oy1+int(45*scale)), "✓", fill=(59, 130, 246), font=get_font(int(26*scale), bold=True), anchor="mm")

    elif content_type == 'leaderboard':
        # Leaderboard UI
        draw.text((sx1+int(40*scale), sy1+int(50*scale)), "Global Rankings", fill=(241, 245, 249), font=get_font(int(28*scale), bold=True))

        # Top 3 Podium Cards
        # Rank 2 (Left)
        draw_rounded_rect(draw, [sx1+int(40*scale), sy1+int(120*scale), sx1+int(240*scale), sy1+int(350*scale)], radius=int(24*scale), fill=(30, 41, 59), outline=(71, 85, 105), width=1)
        draw.text((sx1+int(140*scale), sy1+int(160*scale)), "🥈 #2", fill=(203, 213, 225), font=get_font(int(24*scale), bold=True), anchor="mm")
        draw.text((sx1+int(140*scale), sy1+int(215*scale)), "Chloe C.", fill=(255, 255, 255), font=get_font(int(22*scale), bold=True), anchor="mm")
        draw.text((sx1+int(140*scale), sy1+int(255*scale)), "1,650 pts", fill=(96, 165, 250), font=get_font(int(19*scale)), anchor="mm")
        draw.text((sx1+int(140*scale), sy1+int(295*scale)), "🇬🇧 UK", fill=(148, 163, 184), font=get_font(int(18*scale)), anchor="mm")

        # Rank 1 (Center)
        draw_rounded_rect(draw, [sx1+int(260*scale), sy1+int(90*scale), sx1+sw-int(260*scale), sy1+int(350*scale)], radius=int(28*scale), fill=(30, 58, 138), outline=(234, 179, 8), width=3)
        draw.text((sx1+sw//2, sy1+int(135*scale)), "👑 #1", fill=(250, 204, 21), font=get_font(int(30*scale), bold=True), anchor="mm")
        draw.text((sx1+sw//2, sy1+int(195*scale)), "Ben Foster", fill=(255, 255, 255), font=get_font(int(25*scale), bold=True), anchor="mm")
        draw.text((sx1+sw//2, sy1+int(240*scale)), "1,985 pts", fill=(250, 204, 21), font=get_font(int(22*scale), bold=True), anchor="mm")
        draw.text((sx1+sw//2, sy1+int(285*scale)), "🇺🇸 USA • Master", fill=(226, 232, 240), font=get_font(int(18*scale)), anchor="mm")

        # Rank 3 (Right)
        draw_rounded_rect(draw, [sx1+sw-int(240*scale), sy1+int(120*scale), sx1+sw-int(40*scale), sy1+int(350*scale)], radius=int(24*scale), fill=(30, 41, 59), outline=(71, 85, 105), width=1)
        draw.text((sx1+sw-int(140*scale), sy1+int(160*scale)), "🥉 #3", fill=(251, 146, 60), font=get_font(int(24*scale), bold=True), anchor="mm")
        draw.text((sx1+sw-int(140*scale), sy1+int(215*scale)), "Aisha K.", fill=(255, 255, 255), font=get_font(int(22*scale), bold=True), anchor="mm")
        draw.text((sx1+sw-int(140*scale), sy1+int(255*scale)), "1,422 pts", fill=(96, 165, 250), font=get_font(int(19*scale)), anchor="mm")
        draw.text((sx1+sw-int(140*scale), sy1+int(295*scale)), "🇦🇪 UAE", fill=(148, 163, 184), font=get_font(int(18*scale)), anchor="mm")

        # Rankings list
        rankings = [
            (4, "Sarah Lee", "1,370 pts", "🇨🇦 Canada", "+60 pts"),
            (5, "David Chen", "1,360 pts", "🇸🇬 Singapore", "+50 pts"),
            (6, "Liam O'Neill", "1,320 pts", "🇦🇺 Australia", "+50 pts"),
            (7, "Sophie Garcia", "1,260 pts", "🇪🇸 Spain", "+50 pts"),
            (8, "Kenji Tanaka", "1,180 pts", "🇯🇵 Japan", "+40 pts")
        ]
        list_y = sy1 + int(380*scale)
        for idx, (rank, name, score, country, gain) in enumerate(rankings):
            ry1 = list_y + idx * int(115*scale)
            ry2 = ry1 + int(98*scale)
            rx1, rx2 = sx1+int(40*scale), sx1+sw-int(40*scale)

            draw_rounded_rect(draw, [rx1, ry1, rx2, ry2], radius=int(22*scale), fill=(30, 41, 59), outline=(51, 65, 85), width=1)
            draw.text((rx1+int(35*scale), ry1+int(49*scale)), f"#{rank}", fill=(148, 163, 184), font=get_font(int(24*scale), bold=True), anchor="mm")
            draw.text((rx1+int(80*scale), ry1+int(25*scale)), name, fill=(255, 255, 255), font=get_font(int(23*scale), bold=True))
            draw.text((rx1+int(80*scale), ry1+int(60*scale)), country, fill=(148, 163, 184), font=get_font(int(19*scale)))

            draw.text((rx2-int(35*scale), ry1+int(25*scale)), score, fill=(96, 165, 250), font=get_font(int(23*scale), bold=True), anchor="ra")
            draw.text((rx2-int(35*scale), ry1+int(60*scale)), gain, fill=(34, 197, 94), font=get_font(int(19*scale), bold=True), anchor="ra")

    # Bottom Nav Bar (Every screen)
    nav_h = int(90 * scale)
    nav_y1, nav_y2 = sy1 + sh - nav_h, sy1 + sh
    draw_rounded_rect(draw, [sx1, nav_y1, sx1+sw, nav_y2], radius=0, fill=(15, 23, 42))
    draw.line([(sx1, nav_y1), (sx1+sw, nav_y1)], fill=(30, 41, 59), width=1)

    active_idx = 0 if content_type == 'vocabulary' else (2 if content_type == 'exam' else 3)
    nav_items = [("Discover", "🔍"), ("Words", "📖"), ("Exams", "📝"), ("Leaders", "🏆"), ("Profile", "👤")]
    step = sw // 5
    for idx, (label, icon) in enumerate(nav_items):
        nx = sx1 + int(step * (idx + 0.5))
        active = (idx == active_idx)
        color = (59, 130, 246) if active else (100, 116, 139)
        draw.text((nx, nav_y1+int(25*scale)), icon, fill=color, font=get_font(int(24*scale)), anchor="mm")
        draw.text((nx, nav_y1+int(58*scale)), label, fill=color, font=get_font(int(16*scale), bold=active), anchor="mm")

    img.save(out_path, 'PNG', optimize=True)
    print(f"Saved: {out_path} ({out_w}x{out_h} px)")

# ==============================================================================
# MAIN EXECUTION
# ==============================================================================
if __name__ == '__main__':
    print("--- Building App Icon & Feature Graphic ---")
    build_app_icon()
    build_feature_graphic()

    print("\n--- Building Phone Screenshots (9:16 Ratio, 1080 x 1920 px) ---")
    render_916_screenshot("VOCABMASTER FLASHCARDS", "SMART & EFFECTIVE VOCABULARY LEARNING", 'vocabulary', 1080, 1920, 'store_assets/phone_screenshot_1_vocabulary.png')
    render_916_screenshot("VOCABMASTER MOCK EXAMS", "REALISTIC TIMED PRACTICE TESTS", 'exam', 1080, 1920, 'store_assets/phone_screenshot_2_exam.png')
    render_916_screenshot("VOCABMASTER LEADERBOARD", "COMPETE WITH LEARNERS WORLDWIDE", 'leaderboard', 1080, 1920, 'store_assets/phone_screenshot_3_leaderboard.png')

    print("\n--- Building 7-inch Tablet Screenshots (9:16 Ratio, 1200 x 1920 px - 2 Screenshots) ---")
    render_916_screenshot("VOCABMASTER FOR TABLET", "SMART & EFFECTIVE VOCABULARY LEARNING", 'vocabulary', 1200, 1920, 'store_assets/tablet_7inch_screenshot_1_vocabulary.png')
    render_916_screenshot("VOCABMASTER FOR TABLET", "REALISTIC TIMED PRACTICE TESTS", 'exam', 1200, 1920, 'store_assets/tablet_7inch_screenshot_2_exam.png')

    print("\n--- Building 10-inch Tablet Screenshots (9:16 Ratio, 1600 x 2560 px - 2 Screenshots) ---")
    render_916_screenshot("VOCABMASTER FOR 10\" TABLET", "SMART & EFFECTIVE VOCABULARY LEARNING", 'vocabulary', 1600, 2560, 'store_assets/tablet_10inch_screenshot_1_vocabulary.png')
    render_916_screenshot("VOCABMASTER FOR 10\" TABLET", "REALISTIC TIMED PRACTICE TESTS", 'exam', 1600, 2560, 'store_assets/tablet_10inch_screenshot_2_exam.png')

    print("\n[SUCCESS] ALL GOOGLE PLAY STORE ASSETS AND LAUNCHER ICONS BUILT SUCCESSFULLY!")
