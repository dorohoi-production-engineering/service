## dorohoi-production-engineering Service

**The Service** is a lightweight weather tracking application designed for anonymous users. Users are automatically identified via cookies and can subscribe to specific cities to monitor real-time weather conditions. When extreme weather events occur, personalized alerts are sent based on the user's subscriptions. **Our Service** is integrated with the [WeatherApi](https://www.weatherapi.com/api-explorer.aspx#current)

### Features

The application replicates essential weather tracking functionalities with the following capabilities:

---

### General Features

- **Anonymous Authentication:** Users are automatically authenticated via cookies — no manual registration required.
- **Subscription System:** Users can subscribe to one or more cities to receive weather updates.
- **Weather Overview:** Real-time weather data is displayed for all subscribed cities.

---

### User Features

- **Manage Subscriptions:** Users can add or remove cities from their subscription list.
- **Weather Alerts:** When extreme weather conditions are detected in one or more subscribed cities (e.g., storms, heatwaves, or heavy snow, see [this](https://www.weatherapi.com/api-explorer.aspx#alerts)), the system sends personalized alerts to the user.
- **Session Persistence:** User subscriptions and session state are maintained via cookies for seamless experience.
