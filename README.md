# 🎓 FBLA 2023-24 CTE Partner Management App (CTE Nexus) 📚

Welcome to the **CTE Nexus** repository, developed by Lucas Andersen for Cleburne. This app is designed to enhance the functionality and user experience in managing partnerships within Career and Technical Education programs.

🏆CTE Nexus was a NLC Qualifier

## Key Features 🔑

### 🔒 Secure Login System
Ensures protected access to user accounts.

### 🔐 Data Security
Utilizes SSL encryption, integrated with Java through Connector/J.

### 📈 Vote-Up System
Allows companies to be voted to the top of the trending list, aiding users in identifying popular and highly-regarded partners.

### 🤖 AI-Powered Search
Intelligent search feature selects the best company from the database based on user queries, powered by Google Gemini API.

### 🔍 Efficient Navigation
Includes search buttons, tabs to sort and filter companies for streamlined browsing.

### 🆘 Interactive Help Page
Provides dynamic assistance to users for an enhanced experience.

### 🛠️ Admin Page
Facilitates user management, including creation and removal of users, and the ability to add or modify data.

### 📊 Report System
Allows creation of customizable reports available in PDF or JPG format for detailed data analysis.

This app is tailored to foster a more integrated and efficient partnership ecosystem, making it easier for staff and partners to collaborate effectively.

## Setup

Database credentials and API keys are loaded from environment variables:

- `DATABASE_URL`
- `DATABASE_USER`
- `DATABASE_PASSWORD`
- `GOOGLE_GEMINI_API_KEY`

Before running the application, ensure these variables are set in your environment.

To help identify any accidentally committed secrets you can run the helper script:

```bash
python scripts/secret_cleaner.py
```

This script scans the repository for common secret patterns and reports any findings.

---

Made with ❤️ by Lucas Andersen
