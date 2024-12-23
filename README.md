# Documentation de l'API de Gestion des Bénéficiaires Effectifs

## Description
Cette API permet de gérer les entités juridiques, les bénéficiaires effectifs, et les relations entre eux. Elle fournit des fonctionnalités pour créer, consulter et analyser la structure d'une entreprise, ainsi que pour identifier les bénéficiaires effectifs possédant plus de 25% des parts.

## Prérequis
- **Java 21**
- **Maven**
- Une base de données H2 intégrée (configurée pour le stockage en mémoire)
- **Postman** ou un autre outil pour tester les requêtes HTTP

## Installation
1. Clonez le repository :
   ```bash
   git clone https://github.com/FinichMohamed/BpiCapitalAnalysis.git
   cd BpiCapitalAnalysis
   ```

2. Compilez et lancez l'application :
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. Accédez à la console H2 pour visualiser la base de données :
    - URL : `http://localhost:8080/h2-console`
    - JDBC URL : `jdbc:h2:mem:bpifrance_db`
    - Nom d'utilisateur : `sa`
    - Mot de passe : vide

## Endpoints de l'API

### Entreprises
- **Créer une entreprise**
    - Méthode : `POST`
    - URL : `/api/companies`
    - Exemple de corps JSON :
      ```json
      {
        "name": "Nom de l'entreprise"
      }
      ```

- **Récupérer une entreprise par ID**
    - Méthode : `GET`
    - URL : `/api/companies/{id}`

- **Ajouter un bénéficiaire à une entreprise**
    - Méthode : `POST`
    - URL : `/api/companies/{id}/beneficiaries`
    - Exemple de corps JSON :
      ```json
      {
        "beneficiaryId": 2,
        "beneficiaryType": "PERSON", // ou "COMPANY"
        "sharePercentage": 30.0
      }
      ```

- **Lister les bénéficiaires**
    - Méthode : `GET`
    - URL : `/api/companies/{id}/beneficiaries`
    - Paramètre: `type` (valeurs possibles : `ALL`, `PERSON`, `COMPANY`)
    - Exemple d'appel :
      ```http
      GET /api/companies/1/beneficiaries?type=ALL
      ```

### Personnes
- **Créer une personne**
    - Méthode : `POST`
    - URL : `/api/persons`
      ```

### Exemple de workflow
1. Créez une entreprise.
2. Créez une ou plusieurs personnes.
3. Ajoutez des personnes ou entreprises comme bénéficiaires d'une entreprise.
4. Récupérez les bénéficiaires effectifs pour analyser la structure de l'entreprise.

## Ce qui a été réalisé
- Mise en place de l'API selon les spécifications du métier.
- Base de données H2 pour un déploiement facile et rapide(memory).
- Tests unitaires et TDD pour valider les fonctionnalités clés.
- Validation avancée : Implémenter des validations supplémentaires pour les entrées utilisateur afin d'assurer l'intégrité des données.
- Gestion des erreurs.
- Documentation claire des endpoints de l'API.

## Améliorations souhaitées
- **Sécurité** : Ajouter une couche d'authentification et d'autorisation, par exemple avec Spring Security (OAuth2 ou JWT).
- **Docker** : Conteneuriser l'application pour faciliter son déploiement et son exécution sur différents environnements.
- **Documentation** : Générer une documentation Swagger pour une meilleure interactivité.
- **Tests d'intégration** : Étendre la couverture des tests pour inclure des scénarios plus complexes.
- **Ajouter la persistance des données**

## Limites actuelles
- L'API n'est pas sécurisée.
- Pas de conteneurisation avec Docker pour l'instant.
- La logique métier ne gère pas encore des scénarios très complexes (e.g., plusieurs niveaux de structures d'entreprises entrelacées).
- Les performances sur des bases de données volumineuses n'ont pas été testées.

## Instructions pour le futur
- **Ajout de Docker** : Créer un fichier `Dockerfile` et une configuration `docker-compose` pour inclure une base de données externe si nécessaire.
- **Ajout de la sécurité** : Intégrer Spring Security avec des rôles utilisateur pour gérer les accès.
- **Évolutivité** : Préparer l'application pour qu'elle fonctionne avec une base de données relationnelle comme PostgreSQL ou MySQL dans un environnement de production.
- **Ajout d'Actuator** : Configurer Spring Boot Actuator pour obtenir des points de terminaison de surveillance comme /actuator/health, /actuator/metrics, etc.

## Contact
Pour toute question, contactez :
- **Nom** : FINICH Mohamed
- **Email** : mfinich@gmail.com

---
Merci d'avoir consulté cette documentation !

