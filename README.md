# FakeStoreApp

FakeStoreApp is a sample e-commerce Android application built with a modular clean architecture using Jetpack Compose and Kotlin. It consumes the [Fake Store API](https://fakestoreapi.com/) for all data operations.

## Features

- **Splash & Authentication**  
  - Splash screen checks for stored JWT token and routes to Login or Products  
  - Login screen posts to `/auth/login` and stores token in SharedPreferences  

- **All Products**  
  - Fetches list from `/products`  
  - Displays in a responsive grid  
  - Category filter and text search  

- **Single Product**  
  - Detail screen for a selected item  
  - Shows title, image, price, description  

- **Edit Product**  
  - PUT to `/products/{id}` to update title, price, description, category, image  
  - Shows toast on success or error  

- **Persistent State**  
  - Stores “user_token” in `PreferencesController`  

## Modules

- `core_api`  
  Retrofit interfaces and data transfer objects  

- `core_data`  
  Repositories wrapping API calls into Kotlin Flows  

- `core_domain`  
  Interactors (use-cases) and domain models  

- `core_model`  
  Network request/response models  

- `core_design_system`  
  Design tokens and theme: colors (light/dark `ColorScheme`), typography, shapes/corners, spacing, and the `FakeStoreTheme` composable that wires them together  

- `core_ui`  
  MVI base classes (`ViewModel`, `State`, `Event`, `Effect`) and common Compose components (buttons, cards, chips, text fields, top bar, loading/progress indicators, network image, expandable text) built on top of `core_design_system`  

- `core_resources`  
  Shared strings, colors, dimensions, styles  

- `app_navigation`  
  Navigation graph definitions and route helpers  

- `feature_splash`  
  Splash screen UI and `SplashViewModel`  

- `feature_login`  
  Login screen UI and `LoginViewModel`  

- `feature_all_products`  
  All-products screen UI and `AllProductsScreenViewModel`  

- `feature_single_product`  
  Single-product detail screen UI and `SingleProductViewModel`  

- `feature_edit`  
  Edit-product screen UI and `EditProductViewModel`  

- `app`  
  Application class, Hilt setup, and `AppNavHost`  

## Design System

`core_design_system` centralizes the app's visual language, consumed by `FakeStoreTheme` and the reusable components in `core_ui`:

- **Color** — light/dark `ColorScheme` plus a `ThemeColorsTemplate` for semantic colors not covered by Material's default scheme
- **Typography** — the app's `FakeStoreTypography` type scale
- **Shape** — `AppCorners` (exposed via `LocalCorners`) and `FakeStoreShapes` for consistent corner radii
- **Spacing** — `Spacing` scale (exposed via `LocalSpacing`) for consistent padding/margins
- **Theme** — `FakeStoreTheme` composable wrapping the above into a single Material theme

## Architecture

Follows Clean Architecture + MVI:


- **ViewModels** respond to `Event`s, update `State`, emit one-off `Effect`s.  
- **Interactors** orchestrate business logic and expose `Flow<PartialState>`.  
- **Repositories** call the network and wrap responses in `Flow`.  
- **API** defines HTTP endpoints via Retrofit.

## Usage

1. **Clone**  
   ```bash
   git clone https://github.com/GeoCoo/FakeStoreApp.git
   ```

2. **Login**  
   The Login screen posts to [Fake Store API](https://fakestoreapi.com/)'s `/auth/login` endpoint. Use one of its published [test users](https://fakestoreapi.com/users) to sign in, e.g.:
   ```json
   {
     "username": "jimmie_k",
     "password": "klein*#%*"
   }
   ```
