SELECT COUNT(*)
FROM Client
WHERE Id < 11444191;

SELECT *
FROM Client
WHERE Id IN (11595571, 11701132,
                       55308090, 22225320, 11446392, 11486046, 11523437, 55238717, 11571919, 11591672, 11812258, 11563150);


SELECT TOP 5 Email
FROM Client
WHERE Email IS NOT NULL;

SELECT *
FROM Client
WHERE Email IN (SELECT DISTINCT Email
                FROM Client) AND Phone IN (SELECT DISTINCT Phone
                                           FROM Client
                                           WHERE Phone IS NOT NULL);


SELECT
  Email,
  count(1) AS cnt
FROM Client
GROUP BY Email
ORDER BY cnt;


SELECT
  Phone,
  count(2) AS cnt
FROM Client
GROUP BY Phone
ORDER BY cnt;


SELECT NotificationOptions
FROM Client
GROUP BY NotificationOptions;


SELECT
  Id,
  Name,
  FirstName,
  Phone
FROM Client
WHERE Phone IN ('23000', '79822609102');


SELECT *
FROM Client where Client.Email in('anastasia.zelenskaya@stoloto.ru');


select DISTINCT phone from Client;

SELECT
*
FROM Client
WHERE Phone IN ('79640012719', '79822609102');


SELECT TOP 1 Alpha3Code
FROM Region
WHERE Id = 239 AND Alpha3Code IS NOT NULL;


SELECT ClientVerificationStep.PartnerKYCStepId
FROM ClientVerificationStep
WHERE ClientId IN (11701132, 11595571);

SELECT ClientVerificationStep.Id
FROM ClientVerificationStep
WHERE PartnerKYCStepId = 3
GROUP BY Id;

SELECT
  ClientId,
  PartnerKYCStepId,
  PassDate,
  State
FROM ClientVerificationStep
WHERE ClientId = 55308090;

SELECT State
FROM ClientVerificationStep
WHERE ClientId = 22225320;

SELECT *
FROM ClientVerificationStep
WHERE ClientId = 12026821;

SELECT Created from Client where Email = 'vladvasin86@mail.ru';

SELECT count(*)
FROM ClientVerificationStep
WHERE State = 4;

SELECT ClientVerificationStep.PartnerKYCStepId
FROM ClientVerificationStep
WHERE ClientId = 55542951 AND PassDate IS NOT NULL;


select login from Client where Login = 'Natasha15021@yandex.ru';

select  count(*) from Client where Login is null;

select  count(*) from Client where Client.Phone = '71111111111';

SELECT DISTINCT Id FROM Client;

SELECT * FROM registrationstageupdatedate_copy where user_id = 12889136;