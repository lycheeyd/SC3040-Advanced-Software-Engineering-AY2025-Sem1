# 🚀 Flutter Frontend Development Guide

## Quick Start

### 1. Navigate to Project
```bash
cd /Users/fanyupei/Codes/sc3040/SC3040-Advanced-Software-Engineering-AY2025-Sem1/calowin_ui
```

### 2. Start Development Server
```bash
flutter run -d chrome --web-port 3000
```

### 3. Make Changes
- Edit `.dart` files in your IDE
- Save files (Ctrl+S / Cmd+S)
- **Changes appear automatically** ✨

---

## 🔄 Development Commands

| Action | Command | Description |
|--------|---------|-------------|
| **Hot Reload** | Press `r` | Update running app |
| **Hot Restart** | Press `R` | Full restart |
| **Stop** | Press `q` or `Ctrl+C` | Quit server |

---

## 🛠️ Troubleshooting

### **Hot Reload Not Working?**
```bash
# Stop server (Ctrl+C), then:
flutter clean
flutter pub get
flutter run -d chrome
```

### **Port Conflicts?**
```bash
# Use different port
flutter run -d chrome --web-port 8080
```

### **Build Errors?**
```bash
# Clean build
flutter clean && flutter pub get && flutter run -d chrome
```

---

## 📁 Key Files

```
lib/
├── common/
│   ├── colors_and_fonts.dart    # Colors & fonts
│   └── input_field.dart        # Input components
├── Pages/
│   ├── sign_up/signup_page.dart
│   └── profile/changepassword_page.dart
└── main.dart                   # App entry
```

---

## 🎨 Design Standards

- **Colors**: Primary Green `#2D5A2D`, Dull Green `#4A6741`
- **Font**: Inter (Google Fonts)
- **Border Radius**: 8px, 12px, 16px
- **Spacing**: 8px, 16px, 24px grid

---

## 🆘 Emergency Reset

```bash
# Kill all Flutter processes
pkill -f flutter

# Clean everything
flutter clean
rm -rf .dart_tool/ build/

# Fresh start
flutter pub get
flutter run -d chrome
```

---

**🎉 Ready to code!** App runs at `http://localhost:3000`