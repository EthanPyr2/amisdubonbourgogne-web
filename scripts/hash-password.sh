#!/usr/bin/env bash
# Chiffre un mot de passe en BCrypt pour app.admins (application-local.yaml).
# Le mot de passe est demandé sans être affiché et ne reste pas dans l'historique du terminal.
# Usage : ./scripts/hash-password.sh
set -euo pipefail

command -v htpasswd >/dev/null || { echo "htpasswd est requis (paquet httpd-tools / apache2-utils)" >&2; exit 1; }

read -rsp "Mot de passe : " password; echo
read -rsp "Confirmer    : " confirm; echo
[[ "$password" == "$confirm" ]] || { echo "Les deux saisies ne correspondent pas." >&2; exit 1; }
[[ ${#password} -ge 12 ]] || { echo "12 caractères minimum." >&2; exit 1; }

# htpasswd produit un hash « $2y$ » : on le convertit en « $2a$ », le préfixe attendu par Spring
hash=$(printf '%s\n' "$password" | htpasswd -niBC 12 "" | tr -d ':\n' | sed 's/^\$2y\$/$2a$/')
echo
echo "À copier dans app.admins (guillemets compris) :"
echo "      password: \"{bcrypt}${hash}\""
