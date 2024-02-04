db.createUser({
        user: 'root',
        pwd: 'toor',
        roles: [
            {
                role: 'readWrite',
                db: 'testDB',
            },
        ],
    });

db.createCollection('app_users', { capped: false });

db.app_users.insertMany([
    {
        "username": "ragnar777",
        "dni": "VIKI771012HMCRG093",
        "enabled": true,
        "password": "$2a$10$zXnw.xqdPTu2Afaz3MbIwuofl118MO.zkYlh13Z7x/jPR9gpLJ0Hm", //s3cr3t
        "role":
        {
            "granted_authorities": ["read"]
        }
    },
    {
        "username": "heisenberg",
        "dni": "BBMB771012HMCRR022",
        "enabled": true,
        "password": "$2a$10$0YBlWedEdVUeX8COglEMEuFXTaLichhlnWXP31HNJRgVr/1CWoX8G", //p4sw0rd
        "role":
        {
            "granted_authorities": ["read"]
        }
    },
    {
        "username": "misterX",
        "dni": "GOTW771012HMRGR087",
        "enabled": true,
        "password": "$2a$10$nvh8DJYCqJZIymQ59HzLV.9MmBhcjsAC8vaIKrA9yFhWofX1uvwVK", //misterX123
        "role":
        {
            "granted_authorities": ["read", "write"]
        }
    },
    {
        "username": "neverMore",
        "dni": "WALA771012HCRGR054",
        "enabled": true,
        "password": "$2a$10$/S0SMv1zc0H3CPsl2LkO4u3qlNXLlPGy/C6gAHQMp/9iC8XWai/KS", //4dmIn
        "role":
        {
            "granted_authorities": ["write"]
        }
    }
]);