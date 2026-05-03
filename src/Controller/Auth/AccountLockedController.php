<?php
namespace App\Controller\Auth;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;

class AccountLockedController extends AbstractController
{
    public function index(): Response
    {
        return $this->render('auth/account-locked.html.twig');
    }
}
