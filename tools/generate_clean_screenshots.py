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
    x1, y1, x2, y2 = xy
    draw.rounded_rectangle([x1, y1, x2, y2], radius=radius, fill=fill, outline=outline, width=width)

# ==============================================================================
# SCREENSHOT 1: Vocabulary Flashcard (100% en-US, VocabMaster Branding)
# ==============================================================================
def render_screenshot_1():
    W, H = 1024, 1024
    # Deep gradient background
    img = create_gradient_bg(W, H, (11, 19, 43), (19, 34, 68))
    draw = ImageDraw.Draw(img)

    # Top Brand / Header
    font_brand = get_font(28, bold=True)
    font_sub = get_font(16, bold=False)
    draw.text((W//2, 50), "VOCABMASTER", fill=(99, 179, 237), font=font_brand, anchor="mm")
    draw.text((W//2, 85), "SMART VOCABULARY FLASHCARDS", fill=(160, 174, 192), font=font_sub, anchor="mm")

    # Phone Frame Container (x: 180 to 844, y: 120 to 940)
    fx1, fy1, fx2, fy2 = 212, 120, 812, 940
    # Outer Glow / Frame
    draw_rounded_rect(draw, [fx1, fy1, fx2, fy2], radius=36, fill=(26, 32, 44), outline=(74, 85, 104), width=3)
    draw_rounded_rect(draw, [fx1+12, fy1+12, fx2-12, fy2-12], radius=28, fill=(15, 23, 42))

    # App Bar inside frame
    draw.text((fx1+35, fy1+40), "Unit 4: Advanced Vocab", fill=(241, 245, 249), font=get_font(18, bold=True))
    draw.text((fx2-35, fy1+40), "15 / 20 Learned", fill=(148, 163, 184), font=get_font(15, bold=False), anchor="ra")

    # Progress Bar
    pb_x1, pb_y, pb_x2 = fx1+35, fy1+75, fx2-35
    draw_rounded_rect(draw, [pb_x1, pb_y, pb_x2, pb_y+8], radius=4, fill=(30, 41, 59))
    draw_rounded_rect(draw, [pb_x1, pb_y, pb_x1 + int((pb_x2-pb_x1)*0.75), pb_y+8], radius=4, fill=(59, 130, 246))

    # Main Flashcard (y: 230 to 730)
    cx1, cy1, cx2, cy2 = fx1+40, fy1+105, fx2-40, fy1+620
    draw_rounded_rect(draw, [cx1, cy1, cx2, cy2], radius=24, fill=(30, 41, 59), outline=(51, 65, 85), width=2)

    # Flashcard Content
    # Target Word
    draw.text((W//2, cy1+50), "Resilient", fill=(255, 255, 255), font=get_font(38, bold=True), anchor="mm")
    draw.text((W//2, cy1+95), "/rɪˈzɪl.i.ənt/  🔊", fill=(148, 163, 184), font=get_font(18, bold=False), anchor="mm")

    # Divider
    draw.line([(cx1+40, cy1+125), (cx2-40, cy1+125)], fill=(51, 65, 85), width=1)

    # Meanings Section (en-US)
    font_section = get_font(15, bold=True)
    font_meaning = get_font(19, bold=True)
    font_def = get_font(16, bold=False)

    draw.text((cx1+40, cy1+145), "ENGLISH MEANINGS", fill=(96, 165, 250), font=font_section)
    draw.text((cx1+40, cy1+175), "1. Durable / Tough", fill=(241, 245, 249), font=font_meaning)
    draw.text((cx1+40, cy1+205), "2. Flexible, Quick to Recover", fill=(241, 245, 249), font=font_meaning)

    # Definition & Usage
    draw.text((cx1+40, cy1+250), "DEFINITION & USAGE", fill=(96, 165, 250), font=font_section)
    def_text = "(adj.) Able to recoil or spring back into shape;\nable to withstand or recover quickly from difficult conditions."
    draw.text((cx1+40, cy1+275), def_text, fill=(203, 213, 225), font=font_def)

    # Example Box
    ex_y1 = cy1 + 355
    draw_rounded_rect(draw, [cx1+30, ex_y1, cx2-30, ex_y1+75], radius=12, fill=(15, 23, 42), outline=(30, 58, 138), width=1)
    draw.text((cx1+45, ex_y1+15), "EXAMPLE", fill=(59, 130, 246), font=get_font(13, bold=True))
    draw.text((cx1+45, ex_y1+38), "\"Children are often remarkably resilient to trauma.\"", fill=(226, 232, 240), font=get_font(15, bold=False))

    # Action Buttons under card
    btn_y = fy1 + 640
    # Left Swipe hint
    draw.text((fx1+45, btn_y+15), "◄ SWIPE LEFT", fill=(239, 68, 68), font=get_font(14, bold=True))
    draw.text((fx1+45, btn_y+35), "Keep Learning", fill=(148, 163, 184), font=get_font(12, bold=False))

    # Right Swipe hint
    draw.text((fx2-45, btn_y+15), "SWIPE RIGHT ►", fill=(34, 197, 94), font=get_font(14, bold=True), anchor="ra")
    draw.text((fx2-45, btn_y+35), "Mark Learned", fill=(148, 163, 184), font=get_font(12, bold=False), anchor="ra")

    # Audio / Favorite / Share buttons in center
    draw_rounded_rect(draw, [W//2-80, btn_y+10, W//2-30, btn_y+50], radius=12, fill=(30, 41, 59))
    draw.text((W//2-55, btn_y+30), "🔊", fill=(255, 255, 255), font=get_font(18), anchor="mm")
    
    draw_rounded_rect(draw, [W//2-25, btn_y+10, W//2+25, btn_y+50], radius=12, fill=(30, 41, 59))
    draw.text((W//2, btn_y+30), "⭐", fill=(255, 255, 255), font=get_font(18), anchor="mm")

    draw_rounded_rect(draw, [W//2+30, btn_y+10, W//2+80, btn_y+50], radius=12, fill=(30, 41, 59))
    draw.text((W//2+55, btn_y+30), "🔗", fill=(255, 255, 255), font=get_font(18), anchor="mm")

    # Bottom Navigation Bar (Discover | Words | Exams | Leaders | Profile)
    nav_y1, nav_y2 = fy2-65, fy2-12
    draw_rounded_rect(draw, [fx1+12, nav_y1, fx2-12, nav_y2], radius=0, fill=(15, 23, 42))
    draw.line([(fx1+12, nav_y1), (fx2-12, nav_y1)], fill=(30, 41, 59), width=1)

    nav_items = [("Discover", "🔍", False), ("Words", "📖", True), ("Exams", "📝", False), ("Leaders", "🏆", False), ("Profile", "👤", False)]
    step = (fx2 - fx1 - 24) // 5
    for idx, (label, icon, active) in enumerate(nav_items):
        nx = fx1 + 12 + int(step * (idx + 0.5))
        color = (59, 130, 246) if active else (100, 116, 139)
        draw.text((nx, nav_y1+18), icon, fill=color, font=get_font(16), anchor="mm")
        draw.text((nx, nav_y1+38), label, fill=color, font=get_font(12, bold=active), anchor="mm")

    img.save('store_assets/screenshot_1_vocabulary.png')
    print("Screenshot 1 generated with perfection!")

# ==============================================================================
# SCREENSHOT 2: Mock Exam Screen (100% en-US, VocabMaster Branding)
# ==============================================================================
def render_screenshot_2():
    W, H = 1024, 1024
    img = create_gradient_bg(W, H, (15, 23, 42), (30, 41, 59))
    draw = ImageDraw.Draw(img)

    # Top Brand / Header
    font_brand = get_font(28, bold=True)
    font_sub = get_font(16, bold=False)
    draw.text((W//2, 50), "VOCABMASTER MOCK EXAM", fill=(99, 179, 237), font=font_brand, anchor="mm")
    draw.text((W//2, 85), "REALISTIC TIMED PRACTICE TESTS", fill=(160, 174, 192), font=font_sub, anchor="mm")

    # Phone Frame Container
    fx1, fy1, fx2, fy2 = 212, 120, 812, 940
    draw_rounded_rect(draw, [fx1, fy1, fx2, fy2], radius=36, fill=(26, 32, 44), outline=(74, 85, 104), width=3)
    draw_rounded_rect(draw, [fx1+12, fy1+12, fx2-12, fy2-12], radius=28, fill=(15, 23, 42))

    # App Bar with Timer
    draw.text((fx1+35, fy1+40), "Question 14 of 40", fill=(241, 245, 249), font=get_font(18, bold=True))
    draw_rounded_rect(draw, [fx2-165, fy1+28, fx2-35, fy1+60], radius=16, fill=(30, 41, 59), outline=(225, 29, 72), width=1)
    draw.text((fx2-100, fy1+44), "⏱ 03:36:47", fill=(244, 63, 94), font=get_font(14, bold=True), anchor="mm")

    # Progress Bar
    pb_x1, pb_y, pb_x2 = fx1+35, fy1+75, fx2-35
    draw_rounded_rect(draw, [pb_x1, pb_y, pb_x2, pb_y+6], radius=3, fill=(30, 41, 59))
    draw_rounded_rect(draw, [pb_x1, pb_y, pb_x1 + int((pb_x2-pb_x1)*(14/40)), pb_y+6], radius=3, fill=(16, 185, 129))

    # Question Box
    qx1, qy1, qx2, qy2 = fx1+35, fy1+105, fx2-35, fy1+290
    draw_rounded_rect(draw, [qx1, qy1, qx2, qy2], radius=20, fill=(30, 41, 59), outline=(51, 65, 85), width=1)
    
    q_title = "VOCABULARY & PROFICIENCY"
    draw.text((qx1+25, qy1+25), q_title, fill=(59, 130, 246), font=get_font(13, bold=True))

    q_text = "Despite the challenging economic climate, the company managed to maintain its growth by adopting innovative strategies that _______ to its resilience and long-term success."
    # Wrap text manually
    words = q_text.split()
    lines, current_line = [], ""
    font_q = get_font(17, bold=False)
    for word in words:
        test_line = current_line + (" " if current_line else "") + word
        if font_q.getlength(test_line) <= (qx2 - qx1 - 50):
            current_line = test_line
        else:
            lines.append(current_line)
            current_line = word
    if current_line:
        lines.append(current_line)

    for i, l in enumerate(lines):
        draw.text((qx1+25, qy1+55 + i*26), l, fill=(241, 245, 249), font=font_q)

    # Options (A, B, C, D, E)
    options = [
        ("A", "contributed significantly", True),
        ("B", "were attributed only", False),
        ("C", "have to be attributed", False),
        ("D", "was leading effectively", False),
        ("E", "is likely to contribute", False)
    ]

    opt_start_y = fy1 + 315
    for idx, (letter, text, selected) in enumerate(options):
        oy1 = opt_start_y + idx * 72
        oy2 = oy1 + 60
        ox1, ox2 = fx1+35, fx2-35
        
        bg_color = (30, 58, 138) if selected else (30, 41, 59)
        border_color = (59, 130, 246) if selected else (51, 65, 85)
        draw_rounded_rect(draw, [ox1, oy1, ox2, oy2], radius=16, fill=bg_color, outline=border_color, width=2 if selected else 1)

        # Letter badge
        badge_bg = (59, 130, 246) if selected else (51, 65, 85)
        badge_fg = (255, 255, 255) if selected else (148, 163, 184)
        draw_rounded_rect(draw, [ox1+15, oy1+12, ox1+51, oy1+48], radius=10, fill=badge_bg)
        draw.text((ox1+33, oy1+30), letter, fill=badge_fg, font=get_font(16, bold=True), anchor="mm")

        # Option text
        text_fg = (255, 255, 255) if selected else (226, 232, 240)
        draw.text((ox1+68, oy1+30), text, fill=text_fg, font=get_font(16, bold=selected), anchor="lm")

        if selected:
            draw.text((ox2-30, oy1+30), "✓", fill=(59, 130, 246), font=get_font(18, bold=True), anchor="mm")

    # Bottom Control Bar
    btn_y = fy2 - 130
    draw_rounded_rect(draw, [fx1+35, btn_y, fx1+180, btn_y+50], radius=16, fill=(30, 41, 59))
    draw.text((fx1+107, btn_y+25), "◄ Previous", fill=(148, 163, 184), font=get_font(15, bold=True), anchor="mm")

    draw_rounded_rect(draw, [fx2-180, btn_y, fx2-35, btn_y+50], radius=16, fill=(37, 99, 235))
    draw.text((fx2-107, btn_y+25), "Next Question ►", fill=(255, 255, 255), font=get_font(15, bold=True), anchor="mm")

    # Bottom Nav Bar
    nav_y1, nav_y2 = fy2-65, fy2-12
    draw_rounded_rect(draw, [fx1+12, nav_y1, fx2-12, nav_y2], radius=0, fill=(15, 23, 42))
    draw.line([(fx1+12, nav_y1), (fx2-12, nav_y1)], fill=(30, 41, 59), width=1)

    nav_items = [("Discover", "🔍", False), ("Words", "📖", False), ("Exams", "📝", True), ("Leaders", "🏆", False), ("Profile", "👤", False)]
    step = (fx2 - fx1 - 24) // 5
    for idx, (label, icon, active) in enumerate(nav_items):
        nx = fx1 + 12 + int(step * (idx + 0.5))
        color = (59, 130, 246) if active else (100, 116, 139)
        draw.text((nx, nav_y1+18), icon, fill=color, font=get_font(16), anchor="mm")
        draw.text((nx, nav_y1+38), label, fill=color, font=get_font(12, bold=active), anchor="mm")

    img.save('store_assets/screenshot_2_exam.png')
    print("Screenshot 2 generated with perfection!")

# ==============================================================================
# SCREENSHOT 3: Leaderboard Screen (100% en-US, VocabMaster Branding)
# ==============================================================================
def render_screenshot_3():
    W, H = 1024, 1024
    img = create_gradient_bg(W, H, (10, 15, 30), (20, 30, 55))
    draw = ImageDraw.Draw(img)

    # Top Brand / Header
    font_brand = get_font(28, bold=True)
    font_sub = get_font(16, bold=False)
    draw.text((W//2, 50), "VOCABMASTER LEADERBOARD", fill=(99, 179, 237), font=font_brand, anchor="mm")
    draw.text((W//2, 85), "COMPETE WITH LEARNERS WORLDWIDE", fill=(160, 174, 192), font=font_sub, anchor="mm")

    # Phone Frame Container
    fx1, fy1, fx2, fy2 = 212, 120, 812, 940
    draw_rounded_rect(draw, [fx1, fy1, fx2, fy2], radius=36, fill=(26, 32, 44), outline=(74, 85, 104), width=3)
    draw_rounded_rect(draw, [fx1+12, fy1+12, fx2-12, fy2-12], radius=28, fill=(15, 23, 42))

    # App Bar / Filters
    draw.text((fx1+35, fy1+40), "Global Rankings", fill=(241, 245, 249), font=get_font(20, bold=True))
    
    # Filter Pills
    filter_x = fx2 - 35
    draw_rounded_rect(draw, [filter_x-180, fy1+25, filter_x, fy1+55], radius=15, fill=(30, 41, 59))
    draw.text((filter_x-135, fy1+40), "Weekly", fill=(59, 130, 246), font=get_font(13, bold=True), anchor="mm")
    draw.text((filter_x-45, fy1+40), "All-Time", fill=(148, 163, 184), font=get_font(13, bold=False), anchor="mm")

    # Top 3 Podium Cards (y: 110 to 300)
    # Rank 2 (Left)
    draw_rounded_rect(draw, [fx1+35, fy1+130, fx1+190, fy1+290], radius=16, fill=(30, 41, 59), outline=(71, 85, 105), width=1)
    draw.text((fx1+112, fy1+155), "🥈 #2", fill=(203, 213, 225), font=get_font(18, bold=True), anchor="mm")
    draw.text((fx1+112, fy1+195), "Chloe C.", fill=(255, 255, 255), font=get_font(16, bold=True), anchor="mm")
    draw.text((fx1+112, fy1+225), "1,650 pts", fill=(96, 165, 250), font=get_font(14, bold=False), anchor="mm")
    draw.text((fx1+112, fy1+255), "🇬🇧 UK", fill=(148, 163, 184), font=get_font(13), anchor="mm")

    # Rank 1 (Center - Gold)
    draw_rounded_rect(draw, [fx1+205, fy1+100, fx2-205, fy1+290], radius=18, fill=(30, 58, 138), outline=(234, 179, 8), width=2)
    draw.text((W//2, fy1+130), "👑 #1", fill=(250, 204, 21), font=get_font(22, bold=True), anchor="mm")
    draw.text((W//2, fy1+175), "Ben Foster", fill=(255, 255, 255), font=get_font(18, bold=True), anchor="mm")
    draw.text((W//2, fy1+210), "1,985 pts", fill=(250, 204, 21), font=get_font(16, bold=True), anchor="mm")
    draw.text((W//2, fy1+245), "🇺🇸 USA • Master", fill=(226, 232, 240), font=get_font(13), anchor="mm")

    # Rank 3 (Right)
    draw_rounded_rect(draw, [fx2-190, fy1+130, fx2-35, fy1+290], radius=16, fill=(30, 41, 59), outline=(71, 85, 105), width=1)
    draw.text((fx2-112, fy1+155), "🥉 #3", fill=(251, 146, 60), font=get_font(18, bold=True), anchor="mm")
    draw.text((fx2-112, fy1+195), "Aisha K.", fill=(255, 255, 255), font=get_font(16, bold=True), anchor="mm")
    draw.text((fx2-112, fy1+225), "1,422 pts", fill=(96, 165, 250), font=get_font(14, bold=False), anchor="mm")
    draw.text((fx2-112, fy1+255), "🇦🇪 UAE", fill=(148, 163, 184), font=get_font(13), anchor="mm")

    # Leaderboard List (Rank 4 to 8)
    rankings = [
        (4, "Sarah Lee", "1,370 pts", "🇨🇦 Canada", "+60 pts"),
        (5, "David Chen", "1,360 pts", "🇸🇬 Singapore", "+50 pts"),
        (6, "Liam O'Neill", "1,320 pts", "🇦🇺 Australia", "+50 pts"),
        (7, "Sophie Garcia", "1,260 pts", "🇪🇸 Spain", "+50 pts"),
        (8, "Kenji Tanaka", "1,180 pts", "🇯🇵 Japan", "+40 pts")
    ]

    list_start_y = fy1 + 310
    for idx, (rank, name, score, country, gain) in enumerate(rankings):
        ry1 = list_start_y + idx * 80
        ry2 = ry1 + 68
        rx1, rx2 = fx1+35, fx2-35

        draw_rounded_rect(draw, [rx1, ry1, rx2, ry2], radius=16, fill=(30, 41, 59), outline=(51, 65, 85), width=1)

        # Rank number
        draw.text((rx1+25, ry1+34), f"#{rank}", fill=(148, 163, 184), font=get_font(16, bold=True), anchor="mm")

        # Name and country
        draw.text((rx1+60, ry1+20), name, fill=(255, 255, 255), font=get_font(16, bold=True))
        draw.text((rx1+60, ry1+44), country, fill=(148, 163, 184), font=get_font(13))

        # Score and gain
        draw.text((rx2-25, ry1+20), score, fill=(96, 165, 250), font=get_font(16, bold=True), anchor="ra")
        draw.text((rx2-25, ry1+44), gain, fill=(34, 197, 94), font=get_font(13, bold=True), anchor="ra")

    # Bottom Nav Bar (Discover | Words | Exams | Leaders | Profile)
    nav_y1, nav_y2 = fy2-65, fy2-12
    draw_rounded_rect(draw, [fx1+12, nav_y1, fx2-12, nav_y2], radius=0, fill=(15, 23, 42))
    draw.line([(fx1+12, nav_y1), (fx2-12, nav_y1)], fill=(30, 41, 59), width=1)

    nav_items = [("Discover", "🔍", False), ("Words", "📖", False), ("Exams", "📝", False), ("Leaders", "🏆", True), ("Profile", "👤", False)]
    step = (fx2 - fx1 - 24) // 5
    for idx, (label, icon, active) in enumerate(nav_items):
        nx = fx1 + 12 + int(step * (idx + 0.5))
        color = (59, 130, 246) if active else (100, 116, 139)
        draw.text((nx, nav_y1+18), icon, fill=color, font=get_font(16), anchor="mm")
        draw.text((nx, nav_y1+38), label, fill=color, font=get_font(12, bold=active), anchor="mm")

    img.save('store_assets/screenshot_3_leaderboard.png')
    print("Screenshot 3 generated with perfection!")

if __name__ == '__main__':
    render_screenshot_1()
    render_screenshot_2()
    render_screenshot_3()
