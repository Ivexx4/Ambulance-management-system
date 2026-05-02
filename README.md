# Ambulance Management System (based on Traccar)

## Overview

Ambulance Management System is an open source fleet management and GPS tracking backend tailored for ambulance operations. It extends the Traccar server to support ambulance-centric workflows such as ambulance entities, assignments, and operational statuses. It supports 200+ GPS protocols and 2000+ device models, works with major SQL databases, and exposes an easy-to-use REST API.

This project is a derivative work of the Traccar GPS tracking server (Apache 2.0). See the Third-Party Notices section below for details and attributions.

Related components from the original Traccar ecosystem include:

- Traccar Web App: https://github.com/traccar/traccar-web
- Traccar Manager App: https://github.com/traccar/traccar-manager
- Traccar Client App (mobile tracking): https://github.com/traccar/traccar-client

## Ambulance-specific features

- Ambulance registry (name, uniqueId, status, notes)
- Link ambulances to devices and drivers
- Search, pagination, and permissions compatible with Traccar
- Ready for geofencing, reports, and notifications

## General features (inherited from Traccar)

- Real-time GPS tracking
- Driver behaviour monitoring
- Detailed and summary reports
- Geofencing functionality
- Alarms and notifications
- Account and device management
- Email and SMS support

## Build

Follow Traccar's official guide to build from source: https://www.traccar.org/build/

Project name (Gradle): ambulance-management-system

## Third-Party Notices and Attributions

This project includes or derives from the following third-party components:

- Traccar (server): Copyright © Traccar. Licensed under the Apache License, Version 2.0. https://www.traccar.org/
- Traccar Web: Copyright © Traccar. Licensed under the Apache License, Version 2.0. https://github.com/traccar/traccar-web
- Jakarta EE APIs (jakarta.*): Licensed under the Eclipse Public License or other compatible licenses. https://jakarta.ee/
- Liquibase: Licensed under the Apache License, Version 2.0. https://www.liquibase.org/
- FasterXML Jackson: Licensed under the Apache License, Version 2.0. https://github.com/FasterXML/jackson

For full license texts, see LICENSE.txt and NOTICE.txt.

## License

Eclipse Public License, Version 2.0

Licensed under the Apache License, Version 2.0 (the "License");
You may not use this file except in compliance with the License.
You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
