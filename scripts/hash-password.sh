#!/usr/bin/env bash
set -euo pipefail

command -v htpasswd >/dev/null || { echo "htpasswd est requis (paquet httpd-tools / apache2-utils)" >&2; exit 1; }

read -rsp "Mot de passe : " password; echo
read -rsp "Confirmer    : " confirm; echo
[[ "$password" == "$confirm" ]] || { echo "Les deux saisies ne correspondent pas." >&2; exit 1; }
[[ ${#password} -ge 12 ]] || { echo "12 caractères minimum." >&2; exit 1; }

hash=$(printf '%s\n' "$password" | htpasswd -niBC 12 "" | tr -d ':\n' | sed 's/^\$2y\$/$2a$/')
echo
echo "À copier dans app.admins (guillemets compris) :"
echo "      password: \"{bcrypt}${hash}\""
