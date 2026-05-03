<?php
echo 'PHP_SAPI: ' . php_sapi_name() . PHP_EOL;
echo 'SCRIPT_FILENAME: ' . ($_SERVER['SCRIPT_FILENAME'] ?? 'NOT SET') . PHP_EOL;
echo 'APP_ENV: ' . ($_ENV['APP_ENV'] ?? 'NOT SET') . PHP_EOL;
