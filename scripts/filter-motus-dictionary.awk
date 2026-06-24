# Filtre dictionnaire Motus (règles du jeu télévisé) :
# - Longueur 6 à 10 lettres (Motus classique)
# - Noms communs, verbes à l'infinitif, participes présent/passé
# - Exclusion : noms composés, verbes conjugués, formes fléchies interdites
#
# Usage : awk -f filter-motus-dictionary.awk -v min_len=6 -v max_len=10 mots_source.txt

function strip_accents(s) {
    gsub(/[àâä]/, "a", s)
    gsub(/[éèêë]/, "e", s)
    gsub(/[îï]/, "i", s)
    gsub(/[ôö]/, "o", s)
    gsub(/[ùûü]/, "u", s)
    gsub(/ç/, "c", s)
    return s
}

function is_conjugated(word,    n) {
    n = length(word)
    if (n < min_len || n > max_len) {
        return 1
    }
    if (word ~ /-/) {
        return 1
    }
    # Présent / imparfait / impératif / subjonctif présent
    if (word ~ /(aient|aions|ames|ons|ont|ais|ait|ai|as|ez|iez)$/ && n >= 6) {
        return 1
    }
    # Subjonctif imparfait
    if (word ~ /(asse|asses|assent|assiez|assions)$/ && n >= 7) {
        return 1
    }
    # Futur / conditionnel (-er)
    if (word ~ /(erai|erais|erait|erons|erez|eront|era|eras)$/ && n >= 7) {
        return 1
    }
    # Futur / conditionnel (-ir)
    if (word ~ /(irai|irais|irait|irons|irez|iront)$/ && n >= 7) {
        return 1
    }
    # Passé simple / autres terminaisons verbales
    if (word ~ /(irent|erent|umes|ates|at|it|is)$/ && n >= 6) {
        return 1
    }
    # 3e personne du pluriel au présent (hors noms en -ment)
    if (word ~ /ent$/ && word !~ /ment$/ && n >= 7) {
        return 1
    }
    # Passé simple 1re personne du singulier en -a
    if (word ~ /a$/ && n >= 7) {
        return 1
    }
    return 0
}

function motus_difficulty(len) {
    if (len <= 6) {
        return "EASY"
    }
    if (len <= 8) {
        return "MEDIUM"
    }
    return "HARD"
}

BEGIN {
    if (min_len == "") min_len = 6
    if (max_len == "") max_len = 10
}

{
    if ($0 ~ /-/) {
        next
    }

    word = strip_accents(tolower($0))
    gsub(/[^a-z]/, "", word)

    if (word == "") {
        next
    }

    if (is_conjugated(word)) {
        next
    }

    upper = toupper(word)
    if (seen[upper]++) {
        next
    }

    len = length(upper)
    print upper ",FR," motus_difficulty(len)
}
