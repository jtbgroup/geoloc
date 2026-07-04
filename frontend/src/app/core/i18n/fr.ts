import { TranslationKeys } from "./en";

export const fr: TranslationKeys = {
  // ── Header ──────────────────────────────────────────────────────────────────
  header: {
    admin: "Admin",
    users: "Utilisateurs",
    changePassword: "Changer le mot de passe",
    signOut: "Se déconnecter",
    language: "Langue",
    geoSearch: "Recherche",
    geoFeatures: "Données géo",
  },

  // ── Login ───────────────────────────────────────────────────────────────────
  login: {
    title: "geoloc",
    subtitle: "Connectez-vous pour continuer",
    username: "Nom d'utilisateur",
    password: "Mot de passe",
    signIn: "Se connecter",
    usernameRequired: "Le nom d'utilisateur est requis",
    passwordRequired: "Le mot de passe est requis",
    invalidCredentials: "Nom d'utilisateur ou mot de passe invalide.",
  },

  // ── Home ────────────────────────────────────────────────────────────────────
  home: {
    hello: "Bonjour,",
    welcomeBack: "Bienvenue sur votre tableau de bord",
  },

  // ── Change Password ─────────────────────────────────────────────────────────
  changePassword: {
    title: "Changer le mot de passe",
    currentPassword: "Mot de passe actuel",
    newPassword: "Nouveau mot de passe",
    confirmPassword: "Confirmer le nouveau mot de passe",
    submit: "Changer le mot de passe",
    success: "Mot de passe changé avec succès.",
    error:
      "Impossible de changer le mot de passe. Vérifiez votre mot de passe actuel et réessayez.",
  },

  // ── User List ───────────────────────────────────────────────────────────────
  userList: {
    title: "Utilisateurs",
    newUser: "Nouvel utilisateur",
    username: "Nom d'utilisateur",
    password: "Mot de passe",
    roles: "Rôles",
    status: "Statut",
    actions: "Actions",
    enabled: "Actif",
    disabled: "Inactif",
    cancel: "Annuler",
    save: "Enregistrer",
    create: "Créer",
    disable: "Désactiver",
    edit: "Modifier",
    noUsers: "Aucun utilisateur trouvé.",
    errorLoad: "Impossible de charger les utilisateurs",
    errorCreate: "Impossible de créer l'utilisateur",
    errorUpdate: "Impossible de mettre à jour l'utilisateur",
    errorDisable: "Impossible de désactiver l'utilisateur",
  },

  // ── User Form ───────────────────────────────────────────────────────────────
  userForm: {
    createUser: "Créer un utilisateur",
    editUser: "Modifier l'utilisateur",
  },

  geoFeatures: {
    title: "Entités géographiques",
    newFeature: "Nouvelle entité",
    createFeature: "Créer une entité",
    editFeature: "Modifier l'entité",
    name: "Nom",
    featureClass: "Classe",
    featureCode: "Code",
    sourceId: "Source",
    latitude: "Latitude",
    longitude: "Longitude",
    coordinates: "Coordonnées",
    actions: "Actions",
    cancel: "Annuler",
    save: "Enregistrer",
    create: "Créer",
    edit: "Modifier",
    delete: "Supprimer",
    noFeatures: "Aucune entité trouvée.",
    errorLoad: "Impossible de charger les entités",
    errorCreate: "Impossible de créer l'entité",
    errorUpdate: "Impossible de mettre à jour l'entité",
    errorDelete: "Impossible de supprimer l'entité",
  },

  geoSearch: {
    title: "Rechercher un lieu",
    placeholder: "Rechercher un pays, une ville, un port, un aéroport…",
    noResults: "Aucun résultat trouvé.",
  },
  // ── Languages ───────────────────────────────────────────────────────────────
  languages: {
    en: "Anglais",
    fr: "Français",
  },
};
