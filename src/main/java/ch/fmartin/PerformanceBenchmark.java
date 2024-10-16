package ch.fmartin;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.unbescape.html.HtmlEscape;

import java.util.concurrent.TimeUnit;

import static org.springframework.web.util.HtmlUtils.htmlUnescape;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PerformanceBenchmark {

  @State(Scope.Thread)
  public static class ThreadState {
    public String[] strings = new String[]{
            "20 Minuten: News &amp; Nachrichten aus der Schweiz | Aktuelles &amp; Schlagzeilen",
            "Now on Kickstarter: The &quot;First Stable Desktop Pellet 3D Printer&quot; - 3DPrint.com | The Voice of 3D Printing / Additive Manufacturing",
            "PolySpectra&#39;s neThing.xyz democratizes 3D printing with free, AI-powered CAD software - 3D Printing Industry",
            "Apple @ Work: Ubiquiti launches the industry&#39;s lowest-cost Wi-Fi 7 enterprise access point - 9to5Mac",
            "Amazon.com: Teakhaus Carving Board - Large Wood Cutting Board with Juice Groove and Grip Handles - Reversible Teak Edge Grain Wood - Knife Friendly - FSC Certified: Proteak Edge Grain Teak Cutting Board: Home &amp; Kitchen",
            "Kabelloses Ladegerät für Samsung, HOLYJOY 3 in 1 Qi-Zertifikat Schnellladestation/Dock kompatibel mit Samsung Galaxy S21/S20/Note 20/Note 10, Galaxy Watch 4/3/Active 2/1/LTE, Buds+/Live: Amazon.de: Elektronik &amp; Foto",
            "elago Ladestation Kompatibel mit Samsung Galaxy Watch 6/6 Classic / 5/5 Pro Ladegerät, Nachttischmodus, Kabelmanagement: Amazon.de: Elektronik &amp; Foto",
            "Android Authority: Tech Reviews, News, Buyer&#39;s Guides, Deals, How-To",
            "Android 15 gets a mention in Google&#39;s latest Easter egg",
            "Here&#39;s our first look at Google Maps&#39; new generative AI recommendations",
            "One UI 6.1 disables one of Android&#39;s best notification features by default",
            "Samsung says the Galaxy S24 Ultra&#39;s washed out display is intended behavior",
            "Wildlife Photographer of the Year People&#39;s Choice Award winner - BBC News",
            "Nigara &quot;Ryky x Jet Li&quot; Limited Edition Aogami Super Matt Migaki Tsuchi – Burrfection Store",
            "Hasegawa Pro-PE Lite Black Cutting Board &quot;FPEL&quot; Series – Burrfection Store",
            "I&#39;ve Had 17 Interviews for Four Jobs and It&#39;s Been Exhuasting",
            "Woman Doesn&#39;t Take Liquids Out of Bag at Airport Security, Sparks Debate",
            "Chipolo ONE Point for Google&#39;s Find My Device app - Chipolo",
            "Chosfox - Switch, Mechanical Keyboard &amp; Parts",
            "iOS 17 Cheat Sheet: All Your iPhone&#39;s Latest Features Explained - CNET",
            "Open Source Doesn&#39;t Require Providing Builds | Code Engineered",
            "Possible additional cause for OTA update error: &quot;ERROR Error sending data: timed out&quot; - ESPHome - Home Assistant Community",
            "Schweiz: Einzelkritik &amp; Ergebnisse - connect",
            "Brace yourself Pokémon fans, there&#39;s now a real-life Pokédex with ChatGPT | Creative Bloq",
            "GitHub&#39;s Private RSA SSH Key Mistakenly Exposed in Public Repository",
            "Microsoft Shares New Guidance in the Wake of &#39;Midnight Blizzard&#39; Cyberattack",
            "Building a FullStack Application with Django, Django REST &amp; Next.js - DEV Community",
            "Samsung Galaxy S24 Ultra (1000 GB, Titanium Black, 6.80&quot;, Dual SIM, 200 Mpx, 5G) - digitec",
            "&quot;We’re Going To Do It When It’s Right, With The Right People&quot; - Robert Downey Jr Finally Discusses The Big Sequel - Fortress of Solitude",
            "Seeing a Bambu trend that&#39;s raising questions - Other topics - Bambu Lab Community Forum",
            "&quot;cockroach import db&quot; - Google Search",
            "import pgdump cockroachdb error &quot;no table definition found&quot; - Google Search",
            "release not installed: &quot;no release in storage for object&quot; - Google Search",
            "&#39;The Story of Grafana&#39; documentary: The business of open source | Grafana Labs",
            "Samsung&#39;s official 20,000mAh 45W powerbank is here - GSMArena.com news",
            "BIO Pflanzkartoffeln &quot;Ballerina&quot; - festkochend - Gusta Garden",
            "TalkTalk Mobile EU &amp; US | 29 CHF | 67% Rabatt | Schweiz unlimitiert » Handy Abovergleich » 2024",
            "Samsung&#39;s Galaxy S24 Ultra revealed to be using a lower grade of titanium than Apple&#39;s iPhone 15 Pro - HardwareZone.com.sg",
            "Longer passwords aren&#39;t safe from intensive cracking efforts - Help Net Security",
            "Available Rooms - Hampton Inn &amp; Suites Atlanta-Downtown",
            "10811 Carrying case | König &amp; Meyer",
            "21421 Tragetasche | König &amp; Meyer",
            "ChatGPT users can now bring GPTs into any conversation. Here&#39;s how it will work | Mint",
            "Don&#39;t Contribute to Open Source - YouTube",
            "Python Hash Sets Explained &amp; Demonstrated - Computerphile - YouTube",
            "I&#39;m never using Git the same way again - YouTube",
            "RTX Remix - Official &#39;Remaster the Classics with RTX&#39; Trailer - YouTube",
            "&quot;The mother of all breaches&quot;: 26 billion records found online | Malwarebytes",
            "Welche Handys haben eSIM • Apple, Samsung &amp; Co | mobilezone",
            "K&amp;M 21421 Carrying case for 2 microphone stands « Mikrofonzubehör",
            "A quick look back at Microsoft&#39;s lawsuit against the Linux-based Lindows OS - Neowin",
            "English is becoming Switzerland&#39;s second national language",
            "A wargame experiment pitting AI chatbots against each other ended exactly how you&#39;d expect | PC Gamer",
            "RED Launches World&#39;s First Large Format Global Shutter Cine Cameras | PetaPixel",
            "Why you shouldn&#39;t buy the Galaxy S25 Ultra or the iPhone 16 Pro Max - PhoneArena",
            "Galaxy S24 Ultra&#39;s screen is indeed impressively scratch-resistant - PhoneArena",
            "Withings BeamO is shaping a smartwatch-free future, and it&#39;s accurate",
            "Anakin/Luke/Rey&#39;s Lightsaber - All Versions - Replica Prop by GizmoThrill | Download free STL model | Printables.com",
            "Wann und wie? - Medikamente einnehmen – so geht&#39;s richtig | rbb",
            "How to Clean a Wooden Cutting Board so It&#39;s Germ-Free",
            "Commercial Kitchen – Row &amp; Sons",
            "Mobile Zubehör - Handyhüllen, Ladegeräte &amp; mehr | Samsung Schweiz",
            "Die neusten Samsung Deals &amp; Angebote | Samsung Schweiz",
            "Zubehör &amp; Cases | Galaxy S24 Ultra | Samsung Schweiz",
            "Galaxy S24 Ultra vorbestellen | Preise &amp; Angebot | Samsung CH",
            "Free Personalised Phone &amp; Earbuds Case | Samsung KX | Samsung UK",
            "Samsung Deals: Sales and Offers on TVs, Phones, Laptops &amp; More | Samsung US",
            "Lotion Hand &amp; Körper 20ml",
            "3M™ Secure Click™ Gase-/Dämpfe &amp; Partikel Kombifilter D8094, ABEK1P3 R D, Secure-Click Filteranschluss, Filter gegen organische, anorganische, saure Gase &amp; Dämpfe, Ammoniak &amp; org. Ammoniakderivate, Partikel",
            "Chemikalienschutz | Direkt bestellen bei ERIKS &amp; Produktinfos",
            "3M Gase-/Dämpfe &amp; Partikel Kombinationsfilter 6099, A2B2E2K2HgP3R + Formaldehyd, nur für Vollmasken | MAAGTECHNIC shop CH",
            "Einweghandschuhe | Direkt bestellen bei ERIKS &amp; Produktinfos",
            "Kälte-/Hitzeschutz | Direkt bestellen bei ERIKS &amp; Produktinfos",
            "Mechanischer Schutz | Direkt bestellen bei ERIKS &amp; Produktinfos",
            "Compare Cheap Flights &amp; Book Airline Tickets to Everywhere | Skyscanner",
            "CI/CD &amp; DevOps for Power BI… Are We There Yet? – Paul Turley&#39;s SQL Server BI Blog",
            "Mobile subscription from Sunrise – phone plans &amp; deals",
            "blue Mobile | Unlimitierte Handy-Abos &amp; Angebote | Swisscom",
            "blue Mobile | Handy-Abos &amp; Angebote | Ab 69.90/Mt. | Swisscom",
            "Don&#39;t buy a Samsung Galaxy S24 Ultra, there&#39;s much better value to be found in a different Galaxy | TechRadar",
            "Meta&#39;s new Code Llama 70B takes aim at Github&#39;s Copilot — it&#39;s far better than the original 5-month old Code Llama but I can&#39;t help but wonder if devs will love it | TechRadar",
            "Samsung&#39;s 86-inch Android display has an incredible feature — a 500V capacitor to protect it from voltage spikes | TechRadar",
            "The Apple Vision Pro has already been hacked - Apple says there&#39;s nothing to worry about, but security experts disagree | TechRadar",
            "Mirka Dust-Free Sanding Systems - Tenaru Timber &amp; Finishes Pty Ltd",
            "Failed USB sticks found to contain &#39;defective&#39; memory chips • The Register",
            "Kinetic Labs Gecko Switch Review — ThereminGoat&#39;s Switches",
            "K&amp;M 210/2 Mikrofonstativ schwarz – Thomann Switzerland",
            "K&amp;M 210/9 Mikrofonstativ schwarz – Thomann Switzerland",
            "K&amp;M 252 Mikrofonstativ schwarz – Thomann Switzerland",
            "K&amp;M 259 Mikrofonstativ schwarz – Thomann Switzerland",
            "K&amp;M 27105 Mikrofonstativ – Thomann Switzerland",
            "Samsung Galaxy S24 Ultra marks the end of an era — here&#39;s why | Tom&#39;s Guide",
            "9 ways the iPhone 15 Pro Max beats the Samsung Galaxy S24 Ultra | Tom&#39;s Guide",
            "I test tech gadgets for a living – it&#39;s time to talk about proprietary charging cables | Tom&#39;s Guide",
            "Three million malware-infected smart toothbrushes used in Swiss DDoS attacks — botnet causes millions of euros in damages | Tom&#39;s Hardware",
            "BitLocker encryption broken in 43 seconds with sub-$10 Raspberry Pi Pico — key can be sniffed when using an external TPM | Tom&#39;s Hardware",
            "China&#39;s fastest gaming GPUs get 20% performance boost in GTA V — Moore Threads GPUs continue to close the performance gap thanks to mature drivers | Tom&#39;s Hardware",
            "Amazon sold a fake RTX 4090 FrankenGPU cobbled together using a 4080 GPU and board — scam card was found in a returns pallet deal | Tom&#39;s Hardware",
            "Thinkpad &#39;nubbin&#39; controlled dimmer light uses Raspberry Pi RP2040 and ESP32 | Tom&#39;s Hardware",
            "LENOVO ThinkVision P40w-20 (62C1GAT6CH) ab CHF 1&#39;519.80 bei Toppreise.ch",
            "SAMSUNG Galaxy S24 Ultra, 1.0TB, Titanium Black (SM-S928B) ab CHF 1&#39;398.00 bei Toppreise.ch",
            "Inflation Calculator | Find US Dollar&#39;s Value From 1913-2024",
            "Meet &#39;Smaug-72B&#39;: The new king of open-source AI | VentureBeat",
            "The World&#39;s Richest Countries Across 3 Metrics",
            "Viral video rightly shows how Microsoft&#39;s HoloLens is way ahead of Apple&#39;s Vision Pro | Windows Central",
            "Microsoft Copilot Pro vs. OpenAI&#39;s ChatGPT Plus: Which is worth your $20 a month? | ZDNET"
    };
  }

  @Benchmark
  public void apacheCommonsText(ThreadState state, Blackhole bh) {
    for (int i = 0; i < state.strings.length; i++) {
      bh.consume(StringEscapeUtils.unescapeHtml4(state.strings[i]));
    }
  }

  @Benchmark
  public void jsoup(ThreadState state, Blackhole bh) {
    for (int i = 0; i < state.strings.length; i++) {
      bh.consume(Jsoup.parse(state.strings[i]).text());
    }
  }

  @Benchmark
  public void unbescape(ThreadState state, Blackhole bh) {
    for (int i = 0; i < state.strings.length; i++) {
      bh.consume(HtmlEscape.unescapeHtml(state.strings[i]));
    }
  }

  @Benchmark
  public void spring(ThreadState state, Blackhole bh) {
    for (int i = 0; i < state.strings.length; i++) {
      bh.consume(htmlUnescape(state.strings[i]));
    }
  }

}
