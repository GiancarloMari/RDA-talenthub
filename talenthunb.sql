-- --------------------------------------------------------
-- Host:                         ucka.veleri.hr
-- Server version:               10.5.26-MariaDB-0+deb11u2 - Debian 11
-- Server OS:                    debian-linux-gnu
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Dumping data for table gmari.korisnik: ~6 rows (approximately)
REPLACE INTO `korisnik` (`id`, `ime`, `email`, `lozinka`, `tip`) VALUES
	(1, 'Admin', 'admin@admin', 'admin123', 'admin'),
	(2, 'Ana Poslodavac', 'ana@firma.hr', 'ana123', 'poslodavac'),
	(4, 'Maja Freelancer', 'maja@mail.com', 'maja123', 'freelancer'),
	(5, 'Giancarlo Mari', 'gmari@veleri.hr', 'lozinka1234', 'freelancer'),
	(6, 'Diego Pavlović', 'dpavlovic@veleri.hr', '1234', 'poslodavac'),
	(7, 'Karlo Ilić', 'kilic@veleri.hr', '1234', 'freelancer');

-- Dumping data for table gmari.prijava: ~3 rows (approximately)
REPLACE INTO `prijava` (`id`, `id_freelancer`, `id_projekt`, `ponuda`) VALUES
	(1, 5, 1, 'Mogu započeti odmah i završiti do kraja tjedna. Moj prijedlog je rad po etapama uz vašu povratnu informaciju nakon svake faze.'),
	(2, 5, 2, 'Imam dosta iskustva sa izradom promotivnog materijala.'),
	(3, 5, 4, 'molim te');

-- Dumping data for table gmari.projekt: ~5 rows (approximately)
REPLACE INTO `projekt` (`id`, `naziv`, `opis`, `budzet`, `rok`, `id_poslodavca`, `zavrsen`, `placen`) VALUES
	(1, 'Obnova Stranice', 'obnovi', 1500.00, '15.07.2025', 6, 1, 1),
	(2, 'Izrada promotivnog materijala', 'poster, slika, kartica', 1000.00, '15.08.2025', 6, 1, 1),
	(3, 'Pisanje referata', 'pisanje referata on nečemu', 200.00, '21.08.2025', 6, 1, 1),
	(4, 'referati', 'pisanje referata', 100.00, '10.05.2025', 6, 1, 1),
	(5, 'izrada 3d modela', '3d model za držač čaše', 60.00, '31.08.2025', 6, 0, 0);

-- Dumping data for table gmari.zaposlenje: ~5 rows (approximately)
REPLACE INTO `zaposlenje` (`id`, `id_projekt`, `id_freelancer`) VALUES
	(1, 1, 5),
	(2, 1, 5),
	(3, 1, 5),
	(4, 1, 5),
	(5, 4, 5);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
