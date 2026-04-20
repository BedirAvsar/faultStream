from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    ListFlowable,
    ListItem,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)


ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "docs" / "faultstream-swot-raporu.pdf"
FONT_PATH = Path(r"C:\Windows\Fonts\arial.ttf")
FONT_NAME = "ArialUnicode"


def register_fonts() -> None:
    pdfmetrics.registerFont(TTFont(FONT_NAME, str(FONT_PATH)))


def build_styles():
    styles = getSampleStyleSheet()
    styles.add(
        ParagraphStyle(
            name="ReportTitle",
            parent=styles["Title"],
            fontName=FONT_NAME,
            fontSize=24,
            leading=30,
            alignment=TA_CENTER,
            textColor=colors.HexColor("#0f172a"),
            spaceAfter=12,
        )
    )
    styles.add(
        ParagraphStyle(
            name="ReportSubTitle",
            parent=styles["Normal"],
            fontName=FONT_NAME,
            fontSize=11,
            leading=16,
            alignment=TA_CENTER,
            textColor=colors.HexColor("#475569"),
            spaceAfter=14,
        )
    )
    styles.add(
        ParagraphStyle(
            name="SectionTitle",
            parent=styles["Heading2"],
            fontName=FONT_NAME,
            fontSize=15,
            leading=20,
            textColor=colors.HexColor("#0f172a"),
            spaceBefore=10,
            spaceAfter=8,
        )
    )
    styles.add(
        ParagraphStyle(
            name="CardTitle",
            parent=styles["Heading3"],
            fontName=FONT_NAME,
            fontSize=13,
            leading=16,
            textColor=colors.white,
            spaceAfter=6,
        )
    )
    styles.add(
        ParagraphStyle(
            name="Body",
            parent=styles["BodyText"],
            fontName=FONT_NAME,
            fontSize=10.5,
            leading=15,
            alignment=TA_JUSTIFY,
            textColor=colors.HexColor("#1e293b"),
            spaceAfter=7,
        )
    )
    styles.add(
        ParagraphStyle(
            name="Small",
            parent=styles["BodyText"],
            fontName=FONT_NAME,
            fontSize=9,
            leading=13,
            textColor=colors.HexColor("#475569"),
        )
    )
    return styles


def bullet_list(items, styles):
    return ListFlowable(
        [
            ListItem(Paragraph(item, styles["Body"]), leftIndent=8)
            for item in items
        ],
        bulletType="bullet",
        bulletFontName=FONT_NAME,
        bulletFontSize=10,
        leftIndent=14,
        bulletOffsetY=1,
    )


def swot_card(title, items, header_color, styles):
    header = Paragraph(title, styles["CardTitle"])
    body = bullet_list(items, styles)
    table = Table(
        [[header], [body]],
        colWidths=[8.1 * cm],
        style=TableStyle(
            [
                ("BACKGROUND", (0, 0), (0, 0), header_color),
                ("BACKGROUND", (0, 1), (0, 1), colors.HexColor("#f8fafc")),
                ("BOX", (0, 0), (-1, -1), 0.8, colors.HexColor("#cbd5e1")),
                ("INNERGRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#cbd5e1")),
                ("LEFTPADDING", (0, 0), (-1, -1), 10),
                ("RIGHTPADDING", (0, 0), (-1, -1), 10),
                ("TOPPADDING", (0, 0), (-1, -1), 8),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 8),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
            ]
        ),
    )
    return table


def build_story():
    styles = build_styles()
    story = []

    title = "FaultStream SWOT Analizi Raporu"
    subtitle = (
        "Hazırlanma tarihi: 19 Nisan 2026<br/>"
        "Kapsam: backend, frontend, dokümantasyon, CI ve operasyonel hazırlık değerlendirmesi"
    )

    story.append(Spacer(1, 1.2 * cm))
    story.append(Paragraph(title, styles["ReportTitle"]))
    story.append(Paragraph(subtitle, styles["ReportSubTitle"]))

    intro_data = [
        ["Değerlendirilen yapı", "Spring Boot backend + Next.js dashboard + Docker altyapısı"],
        ["Fiili kod kapsamı", "Ağırlıklı olarak user ve equipment domain'leri"],
        ["Eksik çekirdek alanlar", "Sensor, alert, work order ve maintenance akışlarının uygulama kodu"],
        ["Genel sonuç", "Vizyonu güçlü, ancak ürün çekirdeği hâlâ erken aşamada"],
    ]
    intro_table = Table(
        intro_data,
        colWidths=[4.2 * cm, 11.2 * cm],
        style=TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#eff6ff")),
                ("TEXTCOLOR", (0, 0), (-1, -1), colors.HexColor("#0f172a")),
                ("BOX", (0, 0), (-1, -1), 0.8, colors.HexColor("#bfdbfe")),
                ("INNERGRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#bfdbfe")),
                ("FONTNAME", (0, 0), (-1, -1), FONT_NAME),
                ("FONTSIZE", (0, 0), (-1, -1), 10),
                ("LEADING", (0, 0), (-1, -1), 14),
                ("LEFTPADDING", (0, 0), (-1, -1), 10),
                ("RIGHTPADDING", (0, 0), (-1, -1), 10),
                ("TOPPADDING", (0, 0), (-1, -1), 8),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 8),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
            ]
        ),
    )
    story.append(intro_table)
    story.append(Spacer(1, 0.6 * cm))

    story.append(Paragraph("Yönetici Özeti", styles["SectionTitle"]))
    executive_summary = [
        "FaultStream doğru teknoloji seçimleri üzerine kurulmuş, anlatımı kuvvetli bir endüstriyel IoT platform iskeleti sunuyor.",
        "Backend tarafında güvenlik, migration ve temel CRUD katmanı oluşturulmuş olsa da platformun ana değer önerisi olan gerçek zamanlı sensor akışı ve otonom alarm zinciri henüz tam uygulanmış değil.",
        "Frontend görsel kalite açısından güçlü bir demo etkisi yaratıyor; ancak gerçek API veya SSE akışına bağlı olmadığı için ürün olgunluğu algısı kod gerçekliğinin önüne geçiyor.",
        "Kısa vadede en yüksek getirili yatırım alanları: sensor akışı, dashboard entegrasyonu, auth sertleştirmesi ve test zorunluluğu olan CI hattı.",
    ]
    story.append(bullet_list(executive_summary, styles))

    story.append(Paragraph("Mevcut Durum Tespiti", styles["SectionTitle"]))
    current_state = [
        "Spring Boot tarafında JWT tabanlı auth, kullanıcı rol modeli, equipment CRUD ve Flyway migration zinciri bulunuyor.",
        "SQL migration'ları ileri domain'leri işaret ediyor; ancak Java servis/controller katmanı henüz bu seviyeye ulaşmamış durumda.",
        "Dashboard tarafında tek sayfalık, mock veriyle beslenen güçlü bir operasyon paneli var.",
        "CI hattı yalnızca derleme seviyesinde; test, frontend build ve entegrasyon doğrulamaları eksik.",
        "Çalışma ortamı doğrulamasında Java 21 hedefi ile mevcut Java 17 ortamı arasında sürüm farkı gözlendi.",
    ]
    story.append(bullet_list(current_state, styles))

    story.append(PageBreak())
    story.append(Paragraph("SWOT Matrisi", styles["SectionTitle"]))

    strengths = [
        "Spring Boot, Kafka, Redis, Flyway ve PostgreSQL seçimi problem alanı ile uyumlu.",
        "Ürün vizyonu, domain dili ve yol haritası oldukça net tanımlanmış.",
        "Auth, rol modeli ve migration disiplini gibi temel platform taşları yerinde.",
        "Dashboard tarafı demo ve sunum etkisi açısından güçlü bir başlangıç sunuyor.",
    ]
    weaknesses = [
        "Dokümantasyon ile gerçek implementasyon arasında anlamlı bir boşluk var.",
        "User ve equipment dışındaki ana domain'ler henüz uygulama kodunda tamamlanmamış.",
        "Dashboard gerçek veri akışına bağlı değil; mock verilerle çalışıyor.",
        "Varsayılan JWT secret, eksik DTO validasyonu ve sınırlı test kapsamı kalite riskleri oluşturuyor.",
    ]
    opportunities = [
        "Sensor + Kafka + dashboard entegrasyonu tamamlanırsa ürün güvenilir biçimde canlandırılabilir.",
        "Testcontainers ve tam CI doğrulaması teknik güveni hızla artırabilir.",
        "Observability ve AI modülleri ticari farklılaşma sağlayabilir.",
        "Mevcut görsel kalite POC, yatırımcı ve müşteri sunumlarında ciddi avantaj sağlar.",
    ]
    threats = [
        "README'deki olgun ürün algısı ile kod gerçekliği arasındaki fark güven kaybı yaratabilir.",
        "Güvenlik yapılandırmaları ve validasyon eksikleri ileride daha maliyetli risklere dönüşebilir.",
        "Java ve frontend sürümlerindeki uyumsuzluklar onboarding ve deployment sorunları doğurabilir.",
        "Eksik test ve observability, endüstriyel kullanım beklentilerine karşı zayıf kalabilir.",
    ]

    matrix = Table(
        [
            [
                swot_card("Strengths", strengths, colors.HexColor("#166534"), styles),
                swot_card("Weaknesses", weaknesses, colors.HexColor("#9a3412"), styles),
            ],
            [
                swot_card("Opportunities", opportunities, colors.HexColor("#1d4ed8"), styles),
                swot_card("Threats", threats, colors.HexColor("#991b1b"), styles),
            ],
        ],
        colWidths=[8.1 * cm, 8.1 * cm],
        rowHeights=[8.6 * cm, 8.6 * cm],
        style=TableStyle(
            [
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("LEFTPADDING", (0, 0), (-1, -1), 0),
                ("RIGHTPADDING", (0, 0), (-1, -1), 0),
                ("TOPPADDING", (0, 0), (-1, -1), 0),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 0),
            ]
        ),
    )
    story.append(matrix)
    story.append(Spacer(1, 0.4 * cm))
    story.append(
        Paragraph(
            "Not: SWOT değerlendirmesi proje deposundaki mevcut kod, migration, dokümantasyon ve pipeline sinyallerine dayalıdır.",
            styles["Small"],
        )
    )

    story.append(Paragraph("Öncelikli Aksiyonlar", styles["SectionTitle"]))
    action_items = [
        "Sensor domain'i, Kafka consumer zinciri ve veri kalıcılığını uçtan uca tamamla.",
        "Next.js dashboard'u gerçek REST veya SSE endpoint'lerine bağla ve mock üretimi kaldır.",
        "Auth katmanında varsayılan secret kullanımını bitir, DTO validasyonlarını zorunlu hale getir.",
        "CI hattına backend testleri, frontend build adımı ve mümkünse entegrasyon testleri ekle.",
        "README, roadmap ve package sürümlerini gerçek proje durumu ile senkronize et.",
    ]
    story.append(bullet_list(action_items, styles))

    story.append(Paragraph("Sonuç", styles["SectionTitle"]))
    story.append(
        Paragraph(
            "FaultStream bugün itibarıyla iyi kurgulanmış ve iyi anlatılmış bir ürün çekirdeği niteliğinde. "
            "Başarı için kritik eşik, güçlü vizyonu çalışan sistem davranışıyla eşleştirmek. "
            "Teknik öncelikler doğru sırayla ele alınırsa proje kısa sürede prototip seviyesinden "
            "inandırıcı ürün seviyesine yaklaşabilir.",
            styles["Body"],
        )
    )

    return story


def add_page_number(canvas, doc):
    canvas.saveState()
    canvas.setFont(FONT_NAME, 9)
    canvas.setFillColor(colors.HexColor("#64748b"))
    canvas.drawRightString(19.2 * cm, 1.2 * cm, f"Sayfa {doc.page}")
    canvas.restoreState()


def main() -> None:
    register_fonts()
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    doc = SimpleDocTemplate(
        str(OUTPUT),
        pagesize=A4,
        leftMargin=1.6 * cm,
        rightMargin=1.6 * cm,
        topMargin=1.5 * cm,
        bottomMargin=1.7 * cm,
        title="FaultStream SWOT Analizi Raporu",
        author="Codex",
    )
    doc.build(build_story(), onFirstPage=add_page_number, onLaterPages=add_page_number)
    print(OUTPUT)


if __name__ == "__main__":
    main()
